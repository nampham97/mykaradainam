// data/repository/ReportsRepository.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.local.dao.*
import javax.inject.Inject
import javax.inject.Singleton

data class ReportData(
    val totalRevenue: Long,
    val sessionCount: Int,
    val revenueByRoom: List<RoomRevenueResult>,
    val topItems: List<TopItemResult>,
    val roomStats: List<RoomStatResult>
)

@Singleton
class ReportsRepository @Inject constructor(
    private val sessionDao: RoomSessionDao,
    private val invoiceItemDao: InvoiceItemDao
) {
    suspend fun getReport(startEpoch: Long, endEpoch: Long): ReportData {
        return ReportData(
            totalRevenue = invoiceItemDao.getTotalRevenue(startEpoch, endEpoch) ?: 0L,
            sessionCount = sessionDao.getSessionCount(startEpoch, endEpoch),
            revenueByRoom = invoiceItemDao.getRevenueByRoom(startEpoch, endEpoch),
            topItems = invoiceItemDao.getTopSellingItems(startEpoch, endEpoch),
            roomStats = sessionDao.getRoomStats(startEpoch, endEpoch)
        )
    }

    suspend fun getSalesDataJson(startEpoch: Long, endEpoch: Long): String {
        val items = invoiceItemDao.getTopSellingItems(startEpoch, endEpoch)
        val json = items.map { """{"name":"${it.name}","totalQty":${it.totalQty},"totalRevenue":${it.totalRevenue}}""" }
        return "[${json.joinToString(",")}]"
    }

    suspend fun getReportDataJson(startEpoch: Long, endEpoch: Long, dateLabel: String): String {
        val report = getReport(startEpoch, endEpoch)
        val rooms = report.revenueByRoom.map { """{"room":${it.roomNumber},"revenue":${it.revenue}}""" }
        val items = report.topItems.take(5).map { """{"name":"${it.name}","qty":${it.totalQty}}""" }
        return """{"date":"$dateLabel","totalRevenue":${report.totalRevenue},"sessionCount":${report.sessionCount},"rooms":[${rooms.joinToString(",")}],"topItems":[${items.joinToString(",")}]}"""
    }
}
