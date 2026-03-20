// ui/reports/ReportsViewModel.kt
package com.mykaradainam.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.repository.ReportData
import com.mykaradainam.data.repository.ReportsRepository
import com.mykaradainam.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val selectedTab: Int = 0, // 0=today, 1=month, 2=AI
    val isLoading: Boolean = true,
    val reportData: ReportData? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReport(0)
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
        if (index < 2) loadReport(index)
    }

    private fun loadReport(tab: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val (start, end) = if (tab == 0) {
                todayStartEpoch() to todayEndEpoch()
            } else {
                monthStartEpoch() to monthEndEpoch()
            }
            val data = reportsRepository.getReport(start, end)
            _uiState.update { it.copy(isLoading = false, reportData = data) }
        }
    }
}
