// data/local/dao/RoomSessionDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.RoomSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomSessionDao {
    @Insert
    suspend fun insert(session: RoomSessionEntity): Long

    @Update
    suspend fun update(session: RoomSessionEntity)

    @Query("SELECT * FROM room_sessions WHERE status IN ('ACTIVE', 'INVOICED') AND roomNumber = :roomNumber LIMIT 1")
    suspend fun getActiveSession(roomNumber: Int): RoomSessionEntity?

    @Query("SELECT * FROM room_sessions WHERE status IN ('ACTIVE', 'INVOICED')")
    fun observeActiveSessions(): Flow<List<RoomSessionEntity>>

    @Query("SELECT * FROM room_sessions WHERE status IN ('ACTIVE', 'INVOICED')")
    suspend fun getActiveSessions(): List<RoomSessionEntity>

    @Query("SELECT * FROM room_sessions WHERE id = :id")
    suspend fun getById(id: Long): RoomSessionEntity?

    @Query("""
        SELECT * FROM room_sessions
        WHERE status = 'FINISHED'
        AND createdAt >= :startEpoch AND createdAt < :endEpoch
        ORDER BY createdAt DESC
    """)
    suspend fun getFinishedSessions(startEpoch: Long, endEpoch: Long): List<RoomSessionEntity>

    @Query("""
        SELECT roomNumber, COUNT(*) as sessionCount,
               AVG(endTime - startTime) as avgDuration,
               SUM(endTime - startTime) as totalTime
        FROM room_sessions
        WHERE status = 'FINISHED' AND createdAt >= :startEpoch AND createdAt < :endEpoch
        GROUP BY roomNumber
    """)
    suspend fun getRoomStats(startEpoch: Long, endEpoch: Long): List<RoomStatResult>

    @Query("SELECT COUNT(*) FROM room_sessions WHERE status = 'FINISHED' AND createdAt >= :startEpoch AND createdAt < :endEpoch")
    suspend fun getSessionCount(startEpoch: Long, endEpoch: Long): Int
}

data class RoomStatResult(
    val roomNumber: Int,
    val sessionCount: Int,
    val avgDuration: Long?,
    val totalTime: Long?
)
