// ui/settings/SettingsScreen.kt
package com.mykaradainam.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.BuildConfig
import com.mykaradainam.ui.theme.CatppuccinMocha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Equipment sections
            item {
                EquipmentSection(
                    title = "Thiết bị Phòng 1",
                    equipment = state.room1Equipment,
                    onAdd = { name, kw -> viewModel.addEquipment(1, name, kw) },
                    onDelete = { viewModel.deleteEquipment(it) }
                )
            }
            item {
                EquipmentSection(
                    title = "Thiết bị Phòng 2",
                    equipment = state.room2Equipment,
                    onAdd = { name, kw -> viewModel.addEquipment(2, name, kw) },
                    onDelete = { viewModel.deleteEquipment(it) }
                )
            }

            // Electricity rates
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CatppuccinMocha.Surface0)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Biểu giá điện", color = CatppuccinMocha.Text, fontWeight = FontWeight.SemiBold)
                    Text("QĐ 1279/QĐ-BCT — Hộ kinh doanh, dưới 6kV", color = CatppuccinMocha.Overlay0, fontSize = 11.sp)
                    state.rates.forEach { rate ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rate.tierName, color = CatppuccinMocha.Text, fontSize = 13.sp)
                                Text("${rate.startHour}h - ${rate.endHour}h", color = CatppuccinMocha.Overlay0, fontSize = 11.sp)
                            }
                            Text("${rate.ratePerKwh.toLong()} đ/kWh", color = CatppuccinMocha.Yellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // API Key
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CatppuccinMocha.Surface0)
                        .padding(16.dp)
                ) {
                    Text("Groq API Key", color = CatppuccinMocha.Text, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    val key = BuildConfig.GROQ_API_KEY
                    Text(
                        if (key.length > 8) "${key.take(4)}...${key.takeLast(4)}" else "Chưa cấu hình",
                        color = CatppuccinMocha.Overlay0,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipmentSection(
    title: String,
    equipment: List<com.mykaradainam.data.local.entity.EquipmentEntity>,
    onAdd: (String, Double) -> Unit,
    onDelete: (com.mykaradainam.data.local.entity.EquipmentEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var kw by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thêm thiết bị") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên") }, singleLine = true)
                    OutlinedTextField(
                        value = kw, onValueChange = { kw = it },
                        label = { Text("Công suất (kW)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val power = kw.toDoubleOrNull() ?: return@TextButton
                    onAdd(name, power)
                    name = ""; kw = ""; showDialog = false
                }) { Text("Thêm") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Hủy") } },
            containerColor = CatppuccinMocha.Surface0
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CatppuccinMocha.Surface0)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = CatppuccinMocha.Text, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = { showDialog = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, null, tint = CatppuccinMocha.Green, modifier = Modifier.size(18.dp))
            }
        }

        if (equipment.isEmpty()) {
            Text("Chưa có thiết bị", color = CatppuccinMocha.Overlay0, fontSize = 13.sp)
        }

        equipment.forEach { eq ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${eq.name} — ${eq.powerKw} kW", color = CatppuccinMocha.Subtext1, fontSize = 13.sp)
                IconButton(onClick = { onDelete(eq) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, null, tint = CatppuccinMocha.Red, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
