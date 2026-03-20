// data/local/dao/ElectricityRateDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.ElectricityRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectricityRateDao {
    @Insert
    suspend fun insertAll(rates: List<ElectricityRateEntity>)

    @Update
    suspend fun update(rate: ElectricityRateEntity)

    @Query("SELECT * FROM electricity_rates ORDER BY startHour")
    fun observeAll(): Flow<List<ElectricityRateEntity>>

    @Query("SELECT * FROM electricity_rates ORDER BY startHour")
    suspend fun getAll(): List<ElectricityRateEntity>

    @Query("SELECT COUNT(*) FROM electricity_rates")
    suspend fun count(): Int
}
