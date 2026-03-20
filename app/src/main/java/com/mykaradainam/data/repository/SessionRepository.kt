// data/repository/SessionRepository.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.local.dao.RoomSessionDao
import com.mykaradainam.data.local.entity.RoomSessionEntity
import com.mykaradainam.model.RoomStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: RoomSessionDao
) {
    fun observeActiveSessions(): Flow<List<RoomSessionEntity>> =
        sessionDao.observeActiveSessions()

    suspend fun getActiveSessions(): List<RoomSessionEntity> =
        sessionDao.getActiveSessions()

    suspend fun getActiveSession(roomNumber: Int): RoomSessionEntity? =
        sessionDao.getActiveSession(roomNumber)

    suspend fun startSession(roomNumber: Int): Long {
        val existing = sessionDao.getActiveSession(roomNumber)
        if (existing != null) return existing.id

        val session = RoomSessionEntity(
            roomNumber = roomNumber,
            startTime = System.currentTimeMillis(),
            status = RoomStatus.ACTIVE.name
        )
        return sessionDao.insert(session)
    }

    suspend fun finishSession(roomNumber: Int) {
        val session = sessionDao.getActiveSession(roomNumber) ?: return
        sessionDao.update(
            session.copy(
                endTime = System.currentTimeMillis(),
                status = RoomStatus.FINISHED.name
            )
        )
    }

    suspend fun markInvoiced(sessionId: Long) {
        val session = sessionDao.getById(sessionId) ?: return
        if (session.status == RoomStatus.ACTIVE.name) {
            sessionDao.update(session.copy(status = RoomStatus.INVOICED.name))
        }
    }

    suspend fun getById(id: Long): RoomSessionEntity? = sessionDao.getById(id)
}
