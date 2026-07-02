package com.bloquemae.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bloquemae.MainActivity
import com.bloquemae.R
import com.bloquemae.data.AppDatabase
import com.bloquemae.util.WeekUtils
import kotlinx.coroutines.flow.first

class HabitReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val time = inputData.getString(HabitReminderScheduler.KEY_TIME)

        val db = AppDatabase.get(applicationContext)
        val habitDao = db.habitDao()
        val checkinDao = db.habitCheckinDao()

        val today = WeekUtils.startOfDay()
        val activeHabits = habitDao.activeHabits().first()
        val todayCheckins = checkinDao.checkinsForDate(today).first()
        val doneIds = todayCheckins.filter { it.done }.map { it.habitId }.toSet()
        val pending = activeHabits.filter { it.id !in doneIds }

        if (pending.isNotEmpty()) showNotification(pending.map { it.name })

        if (time != null) HabitReminderScheduler.rescheduleFor(applicationContext, time)
        return Result.success()
    }

    private fun showNotification(pendingNames: List<String>) {
        val context = applicationContext
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val openApp = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_star)
            .setContentTitle(context.getString(R.string.habit_reminder_title))
            .setContentText(context.getString(R.string.habit_reminder_text, pendingNames.joinToString(", ")))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "habit_reminders"
        private const val NOTIFICATION_ID = 1001

        fun createChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.habit_reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
}
