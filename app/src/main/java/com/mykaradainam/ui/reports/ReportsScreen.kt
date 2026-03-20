// ui/reports/ReportsScreen.kt
package com.mykaradainam.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.mykaradainam.ui.components.DonutChart
import com.mykaradainam.ui.components.DonutSlice
import com.mykaradainam.ui.components.ShimmerCard
import com.mykaradainam.ui.theme.AppColors
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("Hôm nay", "Tháng này", "AI Tư vấn")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Báo cáo", fontWeight = FontWeight.Bold) },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tabs
            PrimaryTabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = CatppuccinMocha.Base,
                contentColor = CatppuccinMocha.Mauve
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(title, fontSize = 13.sp) }
                    )
                }
            }

            if (state.selectedTab < 2) {
                // Reports content
                if (state.isLoading) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(3) { ShimmerCard(Modifier.fillMaxWidth().height(100.dp)) }
                    }
                } else {
                    val data = state.reportData ?: return@Column

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Revenue card
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CatppuccinMocha.Surface0)
                                .padding(16.dp)
                        ) {
                            Text("Tổng doanh thu", color = CatppuccinMocha.Overlay0, fontSize = 11.sp, letterSpacing = 1.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(formatVnd(data.totalRevenue), color = CatppuccinMocha.Green, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("${data.sessionCount} phiên", color = CatppuccinMocha.Overlay0, fontSize = 12.sp)
                        }

                        // Revenue by room donut
                        if (data.revenueByRoom.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CatppuccinMocha.Surface0)
                                    .padding(16.dp)
                            ) {
                                Text("Doanh thu theo phòng", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                DonutChart(
                                    slices = data.revenueByRoom.map { r ->
                                        DonutSlice(
                                            label = "Phòng ${r.roomNumber}: ${formatVndShort(r.revenue)}",
                                            value = r.revenue.toFloat(),
                                            color = if (r.roomNumber == 1) AppColors.Room1Color else AppColors.Room2Color
                                        )
                                    },
                                    centerLabel = "2 phòng"
                                )
                            }
                        }

                        // Top items donut
                        if (data.topItems.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CatppuccinMocha.Surface0)
                                    .padding(16.dp)
                            ) {
                                Text("Mặt hàng bán chạy", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                DonutChart(
                                    slices = data.topItems.take(5).mapIndexed { i, item ->
                                        DonutSlice(
                                            label = "${item.name} (${item.totalQty})",
                                            value = item.totalQty.toFloat(),
                                            color = AppColors.ChartPalette[i % AppColors.ChartPalette.size]
                                        )
                                    },
                                    centerLabel = "Top ${minOf(data.topItems.size, 5)}"
                                )
                            }
                        }

                        // Room stats
                        if (data.roomStats.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CatppuccinMocha.Surface0)
                                    .padding(16.dp)
                            ) {
                                Text("Thống kê phòng", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    data.roomStats.forEach { stat ->
                                        Column(
                                            modifier = Modifier.weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(CatppuccinMocha.Base)
                                                .padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "${stat.sessionCount}",
                                                color = if (stat.roomNumber == 1) CatppuccinMocha.Blue else CatppuccinMocha.Mauve,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("Phiên P${stat.roomNumber}", color = CatppuccinMocha.Overlay0, fontSize = 10.sp)
                                            stat.avgDuration?.let {
                                                Text("TB: ${formatDurationShort(it)}", color = CatppuccinMocha.Overlay0, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            } else {
                // AI Advisor tab - Task 14
                AiAdvisorTab()
            }
        }
    }
}
