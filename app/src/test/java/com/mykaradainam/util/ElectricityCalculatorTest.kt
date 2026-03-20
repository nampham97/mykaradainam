// test/util/ElectricityCalculatorTest.kt
package com.mykaradainam.util

import com.mykaradainam.data.local.entity.ElectricityRateEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ElectricityCalculatorTest {
    private val rates = listOf(
        ElectricityRateEntity(1, "Bình thường", 4, 17, 3152.0),
        ElectricityRateEntity(2, "Cao điểm", 17, 20, 5422.0),
        ElectricityRateEntity(3, "Bình thường", 20, 22, 3152.0),
        ElectricityRateEntity(4, "Thấp điểm", 22, 4, 1918.0)
    )

    @Test
    fun `calculate cost for session entirely in normal hours`() {
        // 14:00 - 16:00 = 2 hours normal rate
        val start = makeEpoch(14, 0)
        val end = makeEpoch(16, 0)
        val totalKw = 3.45 // total equipment kW
        val cost = calculateElectricityCost(start, end, totalKw, rates)
        // 3.45 kW * 2h * 3152 = 21,748.8
        assertEquals(21749.0, cost, 1.0)
    }

    @Test
    fun `calculate cost spanning peak hours`() {
        // 16:00 - 19:00 = 1h normal + 2h peak
        val start = makeEpoch(16, 0)
        val end = makeEpoch(19, 0)
        val totalKw = 3.45
        val cost = calculateElectricityCost(start, end, totalKw, rates)
        // 1h * 3152 * 3.45 + 2h * 5422 * 3.45 = 10,874.4 + 37,411.8 = 48,286.2
        assertEquals(48286.0, cost, 1.0)
    }

    private fun makeEpoch(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")).apply {
            set(2026, Calendar.MARCH, 20, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
