// ui/home/HomeScreen.kt
package com.mykaradainam.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.model.RoomStatus
import com.mykaradainam.ui.components.RoomCard
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatDate
import com.mykaradainam.util.formatVndShort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCamera: (sessionId: Long, roomNumber: Int) -> Unit,
    onNavigateToVoice: (sessionId: Long, roomNumber: Int) -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Room picker dialog
    if (state.showRoomPicker) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRoomPicker() },
            title = { Text("Chọn phòng", color = CatppuccinMocha.Text) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.getActiveRooms().forEach { room ->
                        OutlinedButton(
                            onClick = {
                                viewModel.dismissRoomPicker()
                                val action = state.pendingAction
                                if (action == "camera") onNavigateToCamera(room.sessionId!!, room.roomNumber)
                                else onNavigateToVoice(room.sessionId!!, room.roomNumber)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Phòng ${room.roomNumber}")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRoomPicker() }) {
                    Text("Hủy")
                }
            },
            containerColor = CatppuccinMocha.Surface0
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "MyKaraDainam",
                            color = CatppuccinMocha.Mauve,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            formatDate(System.currentTimeMillis()),
                            color = CatppuccinMocha.Overlay0,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Cài đặt", tint = CatppuccinMocha.Overlay1)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Room Cards side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RoomCard(
                    roomNumber = 1,
                    status = state.room1.status,
                    startTime = state.room1.startTime,
                    modifier = Modifier.weight(1f),
                    onStart = { viewModel.startRoom(1) },
                    onFinish = { viewModel.finishRoom(1) },
                    onInvoice = {
                        state.room1.sessionId?.let { sid ->
                            // Show method picker (camera/voice) - simplified: go to camera
                            onNavigateToCamera(sid, 1)
                        }
                    }
                )
                RoomCard(
                    roomNumber = 2,
                    status = state.room2.status,
                    startTime = state.room2.startTime,
                    modifier = Modifier.weight(1f),
                    onStart = { viewModel.startRoom(2) },
                    onFinish = { viewModel.finishRoom(2) },
                    onInvoice = {
                        state.room2.sessionId?.let { sid ->
                            onNavigateToCamera(sid, 2)
                        }
                    }
                )
            }

            // Quick Actions label
            Text(
                "Hành động nhanh",
                color = CatppuccinMocha.Overlay0,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )

            // Quick Action Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Camera
                QuickActionCard(
                    icon = Icons.Default.CameraAlt,
                    label = "Chụp hóa đơn",
                    color = CatppuccinMocha.Blue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val activeRooms = viewModel.getActiveRooms()
                        when (activeRooms.size) {
                            1 -> onNavigateToCamera(activeRooms[0].sessionId!!, activeRooms[0].roomNumber)
                            2 -> viewModel.requestQuickAction("camera")
                            else -> scope.launch { snackbarHostState.showSnackbar("Chưa có phòng hoạt động") }
                        }
                    }
                )
                // Voice
                QuickActionCard(
                    icon = Icons.Default.Mic,
                    label = "Nhập giọng nói",
                    color = CatppuccinMocha.Mauve,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val activeRooms = viewModel.getActiveRooms()
                        when (activeRooms.size) {
                            1 -> onNavigateToVoice(activeRooms[0].sessionId!!, activeRooms[0].roomNumber)
                            2 -> viewModel.requestQuickAction("voice")
                            else -> {}
                        }
                    }
                )
            }

            // Today Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CatppuccinMocha.Surface0)
                    .clickable { onNavigateToReports() }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    value = formatVndShort(state.todayRevenue),
                    label = "Hôm nay",
                    color = CatppuccinMocha.Green
                )
                Box(Modifier.width(1.dp).height(32.dp).background(CatppuccinMocha.Surface1))
                StatItem(
                    value = "${state.todaySessionCount}",
                    label = "Phiên",
                    color = CatppuccinMocha.Blue
                )
                Box(Modifier.width(1.dp).height(32.dp).background(CatppuccinMocha.Surface1))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        tint = CatppuccinMocha.Mauve,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Báo cáo", color = CatppuccinMocha.Overlay0, fontSize = 10.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CatppuccinMocha.Surface0)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatItem(value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = CatppuccinMocha.Overlay0, fontSize = 10.sp)
    }
}
