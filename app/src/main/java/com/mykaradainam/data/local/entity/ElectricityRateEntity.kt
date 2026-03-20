// data/local/entity/ElectricityRateEntity.kt
package com.mykaradainam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "electricity_rates")
data class ElectricityRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tierName: String,
    val startHour: Int,
    val endHour: Int,
    val ratePerKwh: Double
)
