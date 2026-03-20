// data/repository/SharedInvoiceDataHolder.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.remote.groq.InvoiceParseResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedInvoiceDataHolder @Inject constructor() {
    private var _result: InvoiceParseResult? = null

    fun set(result: InvoiceParseResult) { _result = result }
    fun get(): InvoiceParseResult? = _result
    fun clear() { _result = null }
}
