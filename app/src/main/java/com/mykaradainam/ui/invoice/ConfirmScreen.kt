// ui/invoice/ConfirmScreen.kt
package com.mykaradainam.ui.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.mykaradainam.ui.components.LoadingButton
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatVnd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmScreen(
    sessionId: Long,
    roomNumber: Int,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: ConfirmViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác nhận hóa đơn — Phòng $roomNumber", fontSize = 16.sp) },
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
        ) {
            // Warnings banner
            AnimatedVisibility(visible = state.confidence != "high" || state.warnings.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CatppuccinMocha.Yellow.copy(alpha = 0.15f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = CatppuccinMocha.Yellow, modifier = Modifier.size(18.dp))
                    Text(
                        "AI không chắc chắn, vui lòng kiểm tra kỹ",
                        color = CatppuccinMocha.Yellow,
                        fontSize = 12.sp
                    )
                }
            }

            // Items list
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                itemsIndexed(state.items, key = { index, _ -> index }) { index, item ->
                    InvoiceItemRow(
                        item = item,
                        onUpdate = { viewModel.updateItem(index, it) },
                        onDelete = { viewModel.removeItem(index) }
                    )
                }

                item {
                    TextButton(
                        onClick = { viewModel.addItem() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Thêm mục")
                    }
                }
            }

            // Total + mismatch warning + save
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(CatppuccinMocha.Surface0)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = state.hasMismatch) {
                    Text(
                        "Tổng AI (${formatVnd(state.aiTotalAmount)}) khác với tổng tính (${formatVnd(state.computedTotal)})",
                        color = CatppuccinMocha.Yellow,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tổng cộng", color = CatppuccinMocha.Overlay1, fontSize = 14.sp)
                    Text(
                        formatVnd(state.computedTotal),
                        color = CatppuccinMocha.Green,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LoadingButton(
                    text = "Lưu hóa đơn",
                    onClick = { viewModel.save(sessionId) },
                    isLoading = state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = CatppuccinMocha.Green,
                    contentColor = CatppuccinMocha.Crust
                )
            }
        }
    }
}

@Composable
private fun InvoiceItemRow(
    item: EditableItem,
    onUpdate: (EditableItem) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CatppuccinMocha.Surface0)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = item.name,
            onValueChange = { onUpdate(item.copy(name = it)) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Tên", fontSize = 13.sp) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CatppuccinMocha.Surface1,
                focusedBorderColor = CatppuccinMocha.Mauve
            )
        )
        OutlinedTextField(
            value = if (item.quantity > 0) item.quantity.toString() else "",
            onValueChange = { onUpdate(item.copy(quantity = it.toIntOrNull() ?: 0)) },
            modifier = Modifier.width(48.dp),
            placeholder = { Text("SL", fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CatppuccinMocha.Surface1,
                focusedBorderColor = CatppuccinMocha.Mauve
            )
        )
        OutlinedTextField(
            value = if (item.unitPrice > 0) item.unitPrice.toString() else "",
            onValueChange = { onUpdate(item.copy(unitPrice = it.toLongOrNull() ?: 0)) },
            modifier = Modifier.width(80.dp),
            placeholder = { Text("Giá", fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CatppuccinMocha.Surface1,
                focusedBorderColor = CatppuccinMocha.Mauve
            )
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, null, tint = CatppuccinMocha.Red, modifier = Modifier.size(16.dp))
        }
    }
}
