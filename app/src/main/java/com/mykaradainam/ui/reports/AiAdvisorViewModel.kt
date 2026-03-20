// ui/reports/AiAdvisorViewModel.kt
package com.mykaradainam.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.local.dao.ElectricityRateDao
import com.mykaradainam.data.local.dao.EquipmentDao
import com.mykaradainam.data.local.dao.RoomSessionDao
import com.mykaradainam.data.repository.GroqRepository
import com.mykaradainam.data.repository.ReportsRepository
import com.mykaradainam.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class AiAdvisorUiState(
    val electricityCost: Double? = null,
    val electricityBreakdown: String = "",
    val inventoryAdvice: String? = null,
    val dailySummary: String? = null,
    val isLoadingElectricity: Boolean = false,
    val isLoadingInventory: Boolean = false,
    val isLoadingSummary: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiAdvisorViewModel @Inject constructor(
    private val sessionDao: RoomSessionDao,
    private val equipmentDao: EquipmentDao,
    private val electricityRateDao: ElectricityRateDao,
    private val reportsRepository: ReportsRepository,
    private val groqRepository: GroqRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAdvisorUiState())
    val uiState: StateFlow<AiAdvisorUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() {
        calculateElectricity()
        loadInventoryAdvice()
        loadDailySummary()
    }

    private fun calculateElectricity() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingElectricity = true) }
            try {
                val rates = electricityRateDao.getAll()
                val kw1 = equipmentDao.getTotalPowerKw(1) ?: 0.0
                val kw2 = equipmentDao.getTotalPowerKw(2) ?: 0.0

                // Last 7 days
                val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
                cal.add(Calendar.DAY_OF_MONTH, -7)
                val weekStart = cal.timeInMillis
                val now = System.currentTimeMillis()

                val sessions = sessionDao.getFinishedSessions(weekStart, now)
                var totalCost = 0.0
                sessions.forEach { session ->
                    val endTime = session.endTime ?: return@forEach
                    val kw = if (session.roomNumber == 1) kw1 else kw2
                    totalCost += calculateElectricityCost(session.startTime, endTime, kw, rates)
                }

                val breakdown = "7 ngày qua: ${sessions.size} phiên\n" +
                    "Phòng 1: ${String.format("%.1f", kw1)} kW\n" +
                    "Phòng 2: ${String.format("%.1f", kw2)} kW\n" +
                    "Ước tính: ${formatVnd(totalCost.toLong())}"

                _uiState.update {
                    it.copy(isLoadingElectricity = false, electricityCost = totalCost, electricityBreakdown = breakdown)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingElectricity = false) }
            }
        }
    }

    private fun loadInventoryAdvice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInventory = true) }
            try {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
                val now = cal.timeInMillis
                cal.add(Calendar.DAY_OF_MONTH, -7)
                val weekStart = cal.timeInMillis
                val salesData = reportsRepository.getSalesDataJson(weekStart, now)
                val dayOfWeek = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("vi", "VN")) ?: "Hôm nay"

                val response = groqRepository.getAiAdvisorResponse(
                    systemPrompt = "Bạn là trợ lý quản lý quán karaoke. Phân tích dữ liệu bán hàng và đưa ra gợi ý nhập hàng bằng tiếng Việt. Trả lời ngắn gọn, thực tế.",
                    userPrompt = "Dữ liệu bán hàng 7 ngày qua:\n$salesData\n\nHôm nay là $dayOfWeek. Gợi ý nhập hàng cho tuần tới?"
                )
                _uiState.update { it.copy(isLoadingInventory = false, inventoryAdvice = response) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingInventory = false, inventoryAdvice = "Không thể tải gợi ý. Kiểm tra kết nối mạng.") }
            }
        }
    }

    private fun loadDailySummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSummary = true) }
            try {
                val todayData = reportsRepository.getReportDataJson(todayStartEpoch(), todayEndEpoch(), formatDate(System.currentTimeMillis()))
                val yesterdayStart = todayStartEpoch() - 86_400_000L
                val yesterdayData = reportsRepository.getReportDataJson(yesterdayStart, todayStartEpoch(), formatDate(yesterdayStart))

                val response = groqRepository.getAiAdvisorResponse(
                    systemPrompt = "Bạn là trợ lý quản lý quán karaoke. Tóm tắt hoạt động kinh doanh trong ngày bằng tiếng Việt tự nhiên. Ngắn gọn, dễ hiểu, có so sánh với ngày trước nếu có dữ liệu.",
                    userPrompt = "Dữ liệu hôm nay:\n$todayData\n\nDữ liệu hôm qua:\n$yesterdayData\n\nTóm tắt hoạt động hôm nay."
                )
                _uiState.update { it.copy(isLoadingSummary = false, dailySummary = response) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingSummary = false, dailySummary = "Không thể tải tóm tắt.") }
            }
        }
    }
}
