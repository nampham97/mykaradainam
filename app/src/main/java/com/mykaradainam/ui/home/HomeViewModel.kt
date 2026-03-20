// ui/home/HomeViewModel.kt
package com.mykaradainam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.local.entity.RoomSessionEntity
import com.mykaradainam.data.repository.InvoiceRepository
import com.mykaradainam.data.repository.ReportsRepository
import com.mykaradainam.data.repository.SessionRepository
import com.mykaradainam.model.RoomStatus
import com.mykaradainam.util.todayStartEpoch
import com.mykaradainam.util.todayEndEpoch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomState(
    val roomNumber: Int,
    val status: RoomStatus = RoomStatus.FREE,
    val sessionId: Long? = null,
    val startTime: Long? = null
)

data class HomeUiState(
    val room1: RoomState = RoomState(1),
    val room2: RoomState = RoomState(2),
    val todayRevenue: Long = 0L,
    val todaySessionCount: Int = 0,
    val showRoomPicker: Boolean = false,
    val pendingAction: String? = null // "camera" or "voice"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeSessions()
        loadTodayStats()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            sessionRepository.observeActiveSessions().collect { sessions ->
                val room1Session = sessions.find { it.roomNumber == 1 }
                val room2Session = sessions.find { it.roomNumber == 2 }
                _uiState.update { state ->
                    state.copy(
                        room1 = roomStateFrom(1, room1Session),
                        room2 = roomStateFrom(2, room2Session)
                    )
                }
            }
        }
    }

    fun loadTodayStats() {
        viewModelScope.launch {
            val start = todayStartEpoch()
            val end = todayEndEpoch()
            val report = reportsRepository.getReport(start, end)
            _uiState.update {
                it.copy(
                    todayRevenue = report.totalRevenue,
                    todaySessionCount = report.sessionCount
                )
            }
        }
    }

    fun startRoom(roomNumber: Int) {
        viewModelScope.launch {
            sessionRepository.startSession(roomNumber)
        }
    }

    fun finishRoom(roomNumber: Int) {
        viewModelScope.launch {
            sessionRepository.finishSession(roomNumber)
            loadTodayStats()
        }
    }

    fun requestQuickAction(action: String) {
        val state = _uiState.value
        val activeSessions = listOfNotNull(
            state.room1.sessionId?.let { state.room1 }.takeIf { state.room1.status != RoomStatus.FREE },
            state.room2.sessionId?.let { state.room2 }.takeIf { state.room2.status != RoomStatus.FREE }
        )

        when (activeSessions.size) {
            0 -> { /* No active rooms — do nothing */ }
            1 -> {
                _uiState.update { it.copy(pendingAction = null) }
                // Will be handled by the screen via callback
            }
            2 -> _uiState.update { it.copy(showRoomPicker = true, pendingAction = action) }
        }
    }

    fun dismissRoomPicker() {
        _uiState.update { it.copy(showRoomPicker = false, pendingAction = null) }
    }

    fun getActiveRooms(): List<RoomState> {
        val state = _uiState.value
        return listOf(state.room1, state.room2).filter { it.status != RoomStatus.FREE }
    }

    private fun roomStateFrom(roomNumber: Int, session: RoomSessionEntity?): RoomState {
        return if (session != null) {
            RoomState(
                roomNumber = roomNumber,
                status = RoomStatus.valueOf(session.status),
                sessionId = session.id,
                startTime = session.startTime
            )
        } else {
            RoomState(roomNumber = roomNumber)
        }
    }
}
