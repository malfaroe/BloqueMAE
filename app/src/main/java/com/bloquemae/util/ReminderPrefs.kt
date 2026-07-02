package com.bloquemae.util

import android.content.Context

// Reminder times the user configured, stored as "HH:mm" strings (24h, zero-padded).
object ReminderPrefs {
    private const val PREFS_NAME = "habit_reminders"
    private const val KEY_TIMES = "times"

    fun getTimes(context: Context): List<String> =
        prefs(context).getStringSet(KEY_TIMES, emptySet())!!.sorted()

    fun addTime(context: Context, time: String) {
        val current = prefs(context).getStringSet(KEY_TIMES, emptySet())!!.toMutableSet()
        current.add(time)
        prefs(context).edit().putStringSet(KEY_TIMES, current).apply()
    }

    fun removeTime(context: Context, time: String) {
        val current = prefs(context).getStringSet(KEY_TIMES, emptySet())!!.toMutableSet()
        current.remove(time)
        prefs(context).edit().putStringSet(KEY_TIMES, current).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
