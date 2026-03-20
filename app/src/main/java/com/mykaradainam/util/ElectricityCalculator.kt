// util/ElectricityCalculator.kt
package com.mykaradainam.util

import com.mykaradainam.data.local.entity.ElectricityRateEntity
import java.util.*

private val ictZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

fun calculateElectricityCost(
    startEpoch: Long,
    endEpoch: Long,
    totalKw: Double,
    rates: List<ElectricityRateEntity>
): Double {
    if (startEpoch >= endEpoch || totalKw <= 0.0 || rates.isEmpty()) return 0.0

    var totalCost = 0.0
    var current = startEpoch

    while (current < endEpoch) {
        val cal = Calendar.getInstance(ictZone).apply { timeInMillis = current }
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)

        val rate = findRate(currentHour, rates)
        val rateEndHour = rate?.endHour ?: (currentHour + 1)

        // Calculate time until rate tier changes or session ends
        val tierEndCal = Calendar.getInstance(ictZone).apply {
            timeInMillis = current
            set(Calendar.HOUR_OF_DAY, rateEndHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (rateEndHour <= currentHour) add(Calendar.DAY_OF_MONTH, 1)
        }

        val segmentEnd = minOf(tierEndCal.timeInMillis, endEpoch)
        val hours = (segmentEnd - current) / 3_600_000.0
        val ratePerKwh = rate?.ratePerKwh ?: 3152.0

        totalCost += totalKw * hours * ratePerKwh
        current = segmentEnd
    }

    return totalCost
}

private fun findRate(hour: Int, rates: List<ElectricityRateEntity>): ElectricityRateEntity? {
    return rates.find { rate ->
        if (rate.startHour < rate.endHour) {
            hour >= rate.startHour && hour < rate.endHour
        } else {
            // Wraps around midnight (e.g., 22-4)
            hour >= rate.startHour || hour < rate.endHour
        }
    }
}

suspend fun calculateTotalElectricityCost(
    sessions: List<Pair<Long, Long>>, // startEpoch, endEpoch pairs
    equipmentKwByRoom: Map<Int, Double>, // roomNumber -> total kW
    roomNumbers: List<Int>, // parallel to sessions
    rates: List<ElectricityRateEntity>
): Double {
    var total = 0.0
    sessions.forEachIndexed { index, (start, end) ->
        val roomNum = roomNumbers[index]
        val kw = equipmentKwByRoom[roomNum] ?: 0.0
        total += calculateElectricityCost(start, end, kw, rates)
    }
    return total
}
