package com.bloquemae.util

import java.text.SimpleDateFormat
import java.util.*

object WeekUtils {
    private val locale = Locale("es", "CL")

    fun currentWeekStart(): Long {
        val cal = Calendar.getInstance(locale)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun currentWeekEnd(): Long {
        val cal = Calendar.getInstance(locale)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        // If today is after Monday, we need to advance to the NEXT Sunday
        if (Calendar.getInstance(locale).get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.WEEK_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    fun formatWeekRange(start: Long, end: Long): String {
        val fmt = SimpleDateFormat("d MMM", locale)
        return "${fmt.format(Date(start))} – ${fmt.format(Date(end))}"
    }

    fun isBlockExpired(weekEnd: Long): Boolean =
        System.currentTimeMillis() > weekEnd
}
