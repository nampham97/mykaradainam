// data/local/dao/InvoiceItemDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceItemDao {
    @Insert
    suspend fun insertAll(items: List<InvoiceItemEntity>)

    @Query("SELECT * FROM invoice_items WHERE sessionId = :sessionId")
    suspend fun getBySessionId(sessionId: Long): List<InvoiceItemEntity>

    @Query("""
        SELECT SUM(subtotal) FROM invoice_items i
        JOIN room_sessions s ON i.sessionId = s.id
        WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
    """)
    suspend fun getTotalRevenue(startEpoch: Long, endEpoch: Long): Long?

    @Query("""
        SELECT s.roomNumber, SUM(i.subtotal) as revenue
        FROM invoice_items i
        JOIN room_sessions s ON i.sessionId = s.id
        WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
        GROUP BY s.roomNumber
    """)
    suspend fun getRevenueByRoom(startEpoch: Long, endEpoch: Long): List<RoomRevenueResult>

    @Query("""
        SELECT i.name, SUM(i.quantity) as totalQty, SUM(i.subtotal) as totalRevenue
        FROM invoice_items i
        JOIN room_sessions s ON i.sessionId = s.id
        WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
        GROUP BY i.name ORDER BY totalQty DESC LIMIT 10
    """)
    suspend fun getTopSellingItems(startEpoch: Long, endEpoch: Long): List<TopItemResult>

    @Query("""
        SELECT SUM(subtotal) FROM invoice_items WHERE sessionId = :sessionId
    """)
    suspend fun getSessionTotal(sessionId: Long): Long?

    @Query("SELECT * FROM invoice_items WHERE sessionId = :sessionId")
    fun observeBySessionId(sessionId: Long): Flow<List<InvoiceItemEntity>>
}

data class RoomRevenueResult(
    val roomNumber: Int,
    val revenue: Long
)

data class TopItemResult(
    val name: String,
    val totalQty: Int,
    val totalRevenue: Long
)
