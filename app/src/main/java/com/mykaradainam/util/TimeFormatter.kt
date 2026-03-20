// util/TimeFormatter.kt
package com.mykaradainam.util

import java.text.SimpleDateFormat
import java.util.*

private val ictZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun formatDurationShort(millis: Long): String {
    val totalMinutes = millis / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}

fun formatTime(epochMs: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale("vi", "VN")).apply {
        timeZone = ictZone
    }
    return sdf.format(Date(epochMs))
}

fun formatDate(epochMs: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply {
        timeZone = ictZone
    }
    return sdf.format(Date(epochMs))
}

fun todayStartEpoch(): Long {
    val cal = Calendar.getInstance(ictZone).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun todayEndEpoch(): Long = todayStartEpoch() + 86_400_000L

fun monthStartEpoch(): Long {
    val cal = Calendar.getInstance(ictZone).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun monthEndEpoch(): Long {
    val cal = Calendar.getInstance(ictZone).apply {
        add(Calendar.MONTH, 1)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}
