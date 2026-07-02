package com.bloquemae.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// AlarmManager alarms are cleared on reboot — re-arm every configured reminder.
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            HabitReminderScheduler.scheduleAll(context)
        }
    }
}
