package com.bloquemae.util

import java.text.SimpleDateFormat
import java.util.*

object WeekUtils {
    private val locale = Locale("es", "CL")

    // Robust Monday-based week: does not rely on locale's firstDayOfWeek
    fun currentWeekStart(): Long {
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        // Days since Monday: Mon=0, Tue=1, ..., Sun=6
        val daysFromMonday = (dow - Calendar.MONDAY + 7) % 7
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun currentWeekEnd(): Long {
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysToSunday = (Calendar.SUNDAY + 7 - dow) % 7
        cal.add(Calendar.DAY_OF_YEAR, daysToSunday)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun nextSundayNoon(): Long {
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val daysToSunday = (Calendar.SUNDAY + 7 - dow) % 7
        if (daysToSunday > 0) {
            cal.add(Calendar.DAY_OF_YEAR, daysToSunday)
        } else {
            // Today is Sunday — if already past noon, target next Sunday
            if (cal.get(Calendar.HOUR_OF_DAY) >= 12) {
                cal.add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
        cal.set(Calendar.HOUR_OF_DAY, 12)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun formatWeekRange(start: Long, end: Long): String {
        val fmt = SimpleDateFormat("d MMM", locale)
        return "${fmt.format(Date(start))} – ${fmt.format(Date(end))}"
    }

    fun isBlockExpired(weekEnd: Long): Boolean = System.currentTimeMillis() > weekEnd

    fun startOfDay(time: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun previousWeekStart(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentWeekStart()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        return cal.timeInMillis
    }

    fun previousWeekEnd(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentWeekEnd()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        return cal.timeInMillis
    }

    fun daysBetweenInclusive(startDay: Long, endDay: Long): Int =
        if (endDay < startDay) 0 else ((endDay - startDay) / 86_400_000L).toInt() + 1

    private val santiagoTz = TimeZone.getTimeZone("America/Santiago")

    // Next occurrence of hour:minute in America/Santiago — today if still ahead, otherwise tomorrow.
    // Uses an explicit timezone (not the device's) so reminder times match Chile regardless of
    // how the phone is configured.
    fun nextDailyTime(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance(santiagoTz)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }
}
