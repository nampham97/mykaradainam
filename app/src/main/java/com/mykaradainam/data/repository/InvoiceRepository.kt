// data/repository/InvoiceRepository.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.local.dao.InvoiceItemDao
import com.mykaradainam.data.local.entity.InvoiceItemEntity
import com.mykaradainam.data.remote.groq.ParsedItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepository @Inject constructor(
    private val invoiceItemDao: InvoiceItemDao
) {
    suspend fun saveInvoiceItems(sessionId: Long, items: List<ParsedItem>) {
        val entities = items.map { item ->
            InvoiceItemEntity(
                sessionId = sessionId,
                name = item.name,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                subtotal = item.quantity.toLong() * item.unitPrice
            )
        }
        invoiceItemDao.insertAll(entities)
    }

    suspend fun getSessionItems(sessionId: Long) =
        invoiceItemDao.getBySessionId(sessionId)

    suspend fun getSessionTotal(sessionId: Long): Long =
        invoiceItemDao.getSessionTotal(sessionId) ?: 0L
}
