package com.mykaradainam.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    onNavigateToCamera: (sessionId: Long, roomNumber: Int) -> Unit,
    onNavigateToVoice: (sessionId: Long, roomNumber: Int) -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Text("Home - placeholder")
}
