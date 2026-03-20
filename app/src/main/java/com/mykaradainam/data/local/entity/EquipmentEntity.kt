// data/local/entity/EquipmentEntity.kt
package com.mykaradainam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomNumber: Int,
    val name: String,
    val powerKw: Double
)
