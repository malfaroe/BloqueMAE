package com.bloquemae.worker

import android.content.Context
import androidx.work.*
import com.bloquemae.util.WeekUtils
import java.util.concurrent.TimeUnit

object HabitReminderScheduler {
    private const val WORK_NAME = "habit_reminder_daily"
    private const val REMINDER_HOUR = 20 // 8pm

    fun schedule(context: Context) {
        val delay = WeekUtils.nextDailyTime(REMINDER_HOUR) - System.currentTimeMillis()
        if (delay <= 0) return

        val request = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(Constraints.NONE)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
