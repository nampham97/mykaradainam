// ui/settings/SettingsViewModel.kt
package com.mykaradainam.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.local.dao.ElectricityRateDao
import com.mykaradainam.data.local.dao.EquipmentDao
import com.mykaradainam.data.local.entity.ElectricityRateEntity
import com.mykaradainam.data.local.entity.EquipmentEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val room1Equipment: List<EquipmentEntity> = emptyList(),
    val room2Equipment: List<EquipmentEntity> = emptyList(),
    val rates: List<ElectricityRateEntity> = emptyList()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val rateDao: ElectricityRateDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                equipmentDao.observeByRoom(1),
                equipmentDao.observeByRoom(2),
                rateDao.observeAll()
            ) { eq1, eq2, rates ->
                SettingsUiState(eq1, eq2, rates)
            }.collect { _uiState.value = it }
        }
    }

    fun addEquipment(roomNumber: Int, name: String, powerKw: Double) {
        viewModelScope.launch {
            equipmentDao.insert(EquipmentEntity(roomNumber = roomNumber, name = name, powerKw = powerKw))
        }
    }

    fun deleteEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch { equipmentDao.delete(equipment) }
    }

    fun updateRate(rate: ElectricityRateEntity) {
        viewModelScope.launch { rateDao.update(rate) }
    }
}
