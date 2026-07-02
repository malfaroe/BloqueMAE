package com.bloquemae.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

// Fired by AlarmManager at the exact configured time. Hands off to WorkManager
// (no delay) so the actual DB query + notification runs off the main thread.
class HabitReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val time = intent.getStringExtra(HabitReminderScheduler.KEY_TIME) ?: return
        val request = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInputData(workDataOf(HabitReminderScheduler.KEY_TIME to time))
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
