package com.bloquemae.worker

import android.content.Context
import androidx.work.*
import com.bloquemae.util.ReminderPrefs
import com.bloquemae.util.WeekUtils
import java.util.concurrent.TimeUnit

object HabitReminderScheduler {
    const val KEY_TIME = "time"

    fun scheduleAll(context: Context) {
        ReminderPrefs.getTimes(context).forEach { schedule(context, it) }
    }

    // Re-schedules a single "HH:mm" slot — used both when the user adds/edits a
    // reminder and when a fired worker reschedules its own next occurrence.
    fun rescheduleFor(context: Context, time: String) {
        if (time in ReminderPrefs.getTimes(context)) schedule(context, time)
    }

    fun cancel(context: Context, time: String) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(time))
    }

    private fun schedule(context: Context, time: String) {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val delay = WeekUtils.nextDailyTime(hour, minute) - System.currentTimeMillis()
        if (delay <= 0) return

        val request = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(KEY_TIME to time))
            .setConstraints(Constraints.NONE)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName(time),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private fun workName(time: String) = "habit_reminder_$time"
}
