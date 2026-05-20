package com.bloquemae

import android.app.Application
import com.bloquemae.worker.BlockScheduler

class BloqueMAEApp : Application() {
    override fun onCreate() {
        super.onCreate()
        BlockScheduler.schedule(this)
    }
}
