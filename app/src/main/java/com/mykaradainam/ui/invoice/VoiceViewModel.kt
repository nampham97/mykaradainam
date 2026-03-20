// ui/invoice/VoiceViewModel.kt
package com.mykaradainam.ui.invoice

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.remote.groq.InvoiceParseResult
import com.mykaradainam.data.repository.GroqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class VoiceUiState(
    val isRecording: Boolean = false,
    val isProcessing: Boolean = false,
    val recordingDurationMs: Long = 0L,
    val parseResult: InvoiceParseResult? = null,
    val error: String? = null
)

@HiltViewModel
class VoiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val groqRepository: GroqRepository,
    private val sharedInvoiceData: SharedInvoiceDataHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private var recorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var recordingStartTime = 0L

    fun startRecording() {
        val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
        audioFile = file

        recorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setMaxDuration(120_000) // 120s max
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }

        recordingStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isRecording = true) }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        _uiState.update { it.copy(isRecording = false) }
        processAudio()
    }

    private fun processAudio() {
        val file = audioFile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            try {
                val result = groqRepository.processVoiceAudio(file)
                sharedInvoiceData.set(result)
                _uiState.update { it.copy(isProcessing = false, parseResult = result) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isProcessing = false, error = "Không thể xử lý giọng nói. Thử lại hoặc nhập thủ công.")
                }
            }
        }
    }

    fun retry() {
        processAudio()
    }

    override fun onCleared() {
        recorder?.release()
        audioFile?.delete()
    }
}
