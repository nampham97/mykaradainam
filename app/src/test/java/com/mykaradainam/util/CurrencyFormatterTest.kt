// test/util/CurrencyFormatterTest.kt
package com.mykaradainam.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {
    @Test
    fun `format zero`() = assertEquals("₫0", formatVnd(0))

    @Test
    fun `format thousands`() = assertEquals("₫25.000", formatVnd(25000))

    @Test
    fun `format millions`() = assertEquals("₫3.850.000", formatVnd(3850000))

    @Test
    fun `format short millions`() = assertEquals("₫3.9M", formatVndShort(3850000))

    @Test
    fun `format short thousands`() = assertEquals("₫25K", formatVndShort(25000))

    @Test
    fun `format short zero`() = assertEquals("₫0", formatVndShort(0))
}
