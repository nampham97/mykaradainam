// ui/invoice/VoiceScreen.kt
package com.mykaradainam.ui.invoice

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.mykaradainam.ui.components.LoadingButton
import com.mykaradainam.ui.components.TimerDisplay
import com.mykaradainam.ui.theme.CatppuccinMocha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(
    sessionId: Long,
    roomNumber: Int,
    onNavigateToConfirm: () -> Unit,
    onBack: () -> Unit,
    viewModel: VoiceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var hasMicPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        hasMicPermission = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasMicPermission) permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(state.parseResult) {
        if (state.parseResult != null) onNavigateToConfirm()
    }

    // Pulsing animation for recording
    val infiniteTransition = rememberInfiniteTransition(label = "recordPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nhập giọng nói — Phòng $roomNumber", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatppuccinMocha.Base)
            )
        },
        containerColor = CatppuccinMocha.Base
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 4.dp,
                    color = CatppuccinMocha.Mauve
                )
                Spacer(Modifier.height(16.dp))
                Text("Đang xử lý giọng nói...", color = CatppuccinMocha.Overlay1)
            } else if (state.error != null) {
                val error = state.error ?: ""
                Icon(Icons.Default.ErrorOutline, null, tint = CatppuccinMocha.Red, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text(error, color = CatppuccinMocha.Red, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LoadingButton("Thử lại", onClick = { viewModel.retry() }, containerColor = CatppuccinMocha.Mauve)
                    OutlinedButton(onClick = onNavigateToConfirm) { Text("Nhập thủ công") }
                }
            } else {
                // Recording UI
                Text(
                    if (state.isRecording) "Đang ghi âm..." else "Nhấn để ghi âm",
                    color = CatppuccinMocha.Text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Đọc thông tin hóa đơn: tên món, số lượng, giá",
                    color = CatppuccinMocha.Overlay0,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(48.dp))

                // Mic button with pulse
                IconButton(
                    onClick = {
                        if (state.isRecording) viewModel.stopRecording()
                        else viewModel.startRecording()
                    },
                    modifier = Modifier
                        .size(96.dp)
                        .then(if (state.isRecording) Modifier.scale(pulseScale) else Modifier)
                        .background(
                            if (state.isRecording) CatppuccinMocha.Red else CatppuccinMocha.Mauve,
                            CircleShape
                        )
                ) {
                    Icon(
                        if (state.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (state.isRecording) "Dừng" else "Ghi âm",
                        tint = CatppuccinMocha.Crust,
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (state.isRecording) {
                    Spacer(Modifier.height(16.dp))
                    Text("Tối đa 2 phút", color = CatppuccinMocha.Overlay0, fontSize = 12.sp)
                }
            }
        }
    }
}
