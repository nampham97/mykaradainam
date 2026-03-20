// ui/invoice/ConfirmViewModel.kt
package com.mykaradainam.ui.invoice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.remote.groq.InvoiceParseResult
import com.mykaradainam.data.remote.groq.ParsedItem
import com.mykaradainam.data.repository.InvoiceRepository
import com.mykaradainam.data.repository.SessionRepository
import com.mykaradainam.data.repository.SharedInvoiceDataHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditableItem(
    val name: String = "",
    val quantity: Int = 1,
    val unitPrice: Long = 0L
) {
    val subtotal: Long get() = quantity.toLong() * unitPrice
}

data class ConfirmUiState(
    val items: List<EditableItem> = emptyList(),
    val aiTotalAmount: Long = 0L,
    val confidence: String = "high",
    val warnings: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false
) {
    val computedTotal: Long get() = items.sumOf { it.subtotal }
    val hasMismatch: Boolean get() = aiTotalAmount > 0 && computedTotal != aiTotalAmount
}

@HiltViewModel
class ConfirmViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val sessionRepository: SessionRepository,
    private val sharedInvoiceData: SharedInvoiceDataHolder,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfirmUiState())
    val uiState: StateFlow<ConfirmUiState> = _uiState.asStateFlow()

    init {
        loadParseResult(sharedInvoiceData.get())
        sharedInvoiceData.clear()
    }

    fun loadParseResult(result: InvoiceParseResult?) {
        if (result != null) {
            _uiState.update {
                it.copy(
                    items = result.items.map { item ->
                        EditableItem(item.name, item.quantity, item.unitPrice)
                    },
                    aiTotalAmount = result.totalAmount,
                    confidence = result.confidence,
                    warnings = result.warnings
                )
            }
        } else {
            // Manual entry: start with one empty row
            _uiState.update { it.copy(items = listOf(EditableItem())) }
        }
    }

    fun updateItem(index: Int, item: EditableItem) {
        _uiState.update {
            val mutable = it.items.toMutableList()
            mutable[index] = item
            it.copy(items = mutable)
        }
    }

    fun addItem() {
        _uiState.update { it.copy(items = it.items + EditableItem()) }
    }

    fun removeItem(index: Int) {
        _uiState.update {
            val mutable = it.items.toMutableList()
            mutable.removeAt(index)
            it.copy(items = mutable)
        }
    }

    fun save(sessionId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val items = _uiState.value.items.filter { it.name.isNotBlank() }
            val parsedItems = items.map { ParsedItem(it.name, it.quantity, it.unitPrice) }
            invoiceRepository.saveInvoiceItems(sessionId, parsedItems)
            sessionRepository.markInvoiced(sessionId)
            _uiState.update { it.copy(isSaving = false, saved = true) }
        }
    }
}
