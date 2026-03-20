// ui/reports/AiAdvisorTab.kt
package com.mykaradainam.ui.reports

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.ui.components.ShimmerCard
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatVnd

@Composable
fun AiAdvisorTab(viewModel: AiAdvisorViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Summary
        AdvisorCard(
            icon = Icons.Default.Summarize,
            title = "Tóm tắt hôm nay",
            color = CatppuccinMocha.Mauve,
            isLoading = state.isLoadingSummary,
            content = state.dailySummary
        )

        // Electricity Cost
        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CatppuccinMocha.Surface0)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.ElectricBolt, null, tint = CatppuccinMocha.Yellow, modifier = Modifier.size(20.dp))
                Text("Dự đoán tiền điện", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            if (state.isLoadingElectricity) {
                ShimmerCard()
            } else if (state.electricityCost != null) {
                Text(
                    formatVnd(state.electricityCost!!.toLong()),
                    color = CatppuccinMocha.Yellow,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(state.electricityBreakdown, color = CatppuccinMocha.Overlay1, fontSize = 12.sp, lineHeight = 18.sp)
            } else {
                Text("Chưa có dữ liệu. Thêm thiết bị trong Cài đặt.", color = CatppuccinMocha.Overlay0, fontSize = 13.sp)
            }
        }

        // Inventory Advice
        AdvisorCard(
            icon = Icons.Default.Inventory,
            title = "Gợi ý nhập hàng",
            color = CatppuccinMocha.Green,
            isLoading = state.isLoadingInventory,
            content = state.inventoryAdvice
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AdvisorCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: androidx.compose.ui.graphics.Color,
    isLoading: Boolean,
    content: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CatppuccinMocha.Surface0)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Text(title, color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        if (isLoading) {
            ShimmerCard()
        } else {
            Text(
                content ?: "Chưa có dữ liệu.",
                color = CatppuccinMocha.Subtext1,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}
