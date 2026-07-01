package com.bloquemae

import android.app.Application
import com.bloquemae.worker.BlockScheduler
import com.bloquemae.worker.HabitReminderScheduler
import com.bloquemae.worker.HabitReminderWorker

class BloqueMAEApp : Application() {
    override fun onCreate() {
        super.onCreate()
        BlockScheduler.schedule(this)
        HabitReminderWorker.createChannel(this)
        HabitReminderScheduler.schedule(this)
    }
}
