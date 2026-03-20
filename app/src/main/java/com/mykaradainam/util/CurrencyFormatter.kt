// util/CurrencyFormatter.kt
package com.mykaradainam.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val vndSymbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
    groupingSeparator = '.'
}
private val vndFormat = DecimalFormat("#,###", vndSymbols)

fun formatVnd(amount: Long): String = "₫${vndFormat.format(amount)}"

fun formatVndShort(amount: Long): String = when {
    amount >= 1_000_000 -> {
        val m = amount / 100_000.0
        val rounded = Math.round(m) / 10.0
        if (rounded == rounded.toLong().toDouble()) "₫${rounded.toLong()}M"
        else "₫${rounded}M"
    }
    amount >= 1_000 -> "₫${amount / 1000}K"
    else -> "₫$amount"
}
