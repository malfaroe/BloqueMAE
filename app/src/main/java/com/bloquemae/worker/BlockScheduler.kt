package com.bloquemae.worker

import android.content.Context
import androidx.work.*
import com.bloquemae.util.WeekUtils
import java.util.concurrent.TimeUnit

object BlockScheduler {
    private const val WORK_NAME = "close_block_sunday_noon"

    fun schedule(context: Context) {
        val delay = WeekUtils.nextSundayNoon() - System.currentTimeMillis()
        if (delay <= 0) return

        val request = OneTimeWorkRequestBuilder<CloseBlockWorker>()
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
