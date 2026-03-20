// test/util/TimeFormatterTest.kt
package com.mykaradainam.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {
    @Test
    fun `format duration zero`() = assertEquals("00:00:00", formatDuration(0))

    @Test
    fun `format duration hours`() = assertEquals("01:23:45", formatDuration(5025000))

    @Test
    fun `format duration minutes only`() = assertEquals("00:05:30", formatDuration(330000))

    @Test
    fun `format short duration`() = assertEquals("1h 23m", formatDurationShort(5025000))

    @Test
    fun `format short minutes`() = assertEquals("5m", formatDurationShort(330000))
}
