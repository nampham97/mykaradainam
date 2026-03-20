// ui/invoice/CameraViewModel.kt
package com.mykaradainam.ui.invoice

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.remote.groq.InvoiceParseResult
import com.mykaradainam.data.repository.GroqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraUiState(
    val capturedBitmap: Bitmap? = null,
    val isProcessing: Boolean = false,
    val parseResult: InvoiceParseResult? = null,
    val error: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val groqRepository: GroqRepository,
    private val sharedInvoiceData: SharedInvoiceDataHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun onPhotoCaptured(bitmap: Bitmap) {
        _uiState.update { it.copy(capturedBitmap = bitmap) }
        processImage(bitmap)
    }

    private fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            try {
                val result = groqRepository.processInvoiceImage(bitmap)
                sharedInvoiceData.set(result)
                _uiState.update { it.copy(isProcessing = false, parseResult = result) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isProcessing = false, error = "Không thể xử lý ảnh. Thử lại hoặc nhập thủ công.")
                }
            }
        }
    }

    fun retry() {
        _uiState.value.capturedBitmap?.let { processImage(it) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
