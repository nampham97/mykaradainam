// data/local/dao/EquipmentDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.EquipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Insert
    suspend fun insert(equipment: EquipmentEntity): Long

    @Update
    suspend fun update(equipment: EquipmentEntity)

    @Delete
    suspend fun delete(equipment: EquipmentEntity)

    @Query("SELECT * FROM equipment WHERE roomNumber = :roomNumber ORDER BY name")
    fun observeByRoom(roomNumber: Int): Flow<List<EquipmentEntity>>

    @Query("SELECT * FROM equipment WHERE roomNumber = :roomNumber")
    suspend fun getByRoom(roomNumber: Int): List<EquipmentEntity>

    @Query("SELECT SUM(powerKw) FROM equipment WHERE roomNumber = :roomNumber")
    suspend fun getTotalPowerKw(roomNumber: Int): Double?
}
