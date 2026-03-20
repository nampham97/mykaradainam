// data/local/entity/RoomSessionEntity.kt
package com.mykaradainam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_sessions")
data class RoomSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomNumber: Int,
    val startTime: Long,
    val endTime: Long? = null,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis()
)
