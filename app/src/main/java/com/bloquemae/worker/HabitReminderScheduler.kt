package com.bloquemae.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bloquemae.util.ReminderPrefs
import com.bloquemae.util.WeekUtils

object HabitReminderScheduler {
    const val KEY_TIME = "time"

    fun scheduleAll(context: Context) {
        ReminderPrefs.getTimes(context).forEach { schedule(context, it) }
    }

    // Re-arms a single "HH:mm" slot — used when the user adds/edits a reminder
    // and when a fired alarm reschedules its own next occurrence.
    fun rescheduleFor(context: Context, time: String) {
        if (time in ReminderPrefs.getTimes(context)) schedule(context, time)
    }

    fun cancel(context: Context, time: String) {
        val pendingIntent = pendingIntentFor(context, time)
        alarmManager(context).cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun canScheduleExactAlarms(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager(context).canScheduleExactAlarms()

    private fun schedule(context: Context, time: String) {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val triggerAt = WeekUtils.nextDailyTime(hour, minute)
        val pendingIntent = pendingIntentFor(context, time)
        val am = alarmManager(context)

        if (canScheduleExactAlarms(context)) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun pendingIntentFor(context: Context, time: String): PendingIntent {
        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            putExtra(KEY_TIME, time)
        }
        return PendingIntent.getBroadcast(
            context, time.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun alarmManager(context: Context) =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}
