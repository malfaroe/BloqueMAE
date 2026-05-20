package com.bloquemae.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bloquemae.data.AppDatabase
import com.bloquemae.data.Block
import com.bloquemae.data.BlockStatus
import com.bloquemae.util.WeekUtils
import java.util.UUID

class CloseBlockWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.get(applicationContext)
        val blockDao = db.blockDao()
        val taskDao = db.taskDao()

        val active = blockDao.activeBlockOnce()
        if (active != null) {
            val total = taskDao.count(active.id)
            val done = taskDao.doneCount(active.id)
            val pct = if (total > 0) done.toFloat() / total else 0f
            blockDao.update(active.copy(status = BlockStatus.CLOSED, completionPct = pct))

            val undone = taskDao.undoneTasks(active.id)
            val last = blockDao.latestBlock()
            val newBlock = Block(
                number = (last?.number ?: 0) + 1,
                weekStart = WeekUtils.currentWeekStart(),
                weekEnd = WeekUtils.currentWeekEnd()
            )
            blockDao.insert(newBlock)

            if (undone.isNotEmpty()) {
                val carried = undone.mapIndexed { i, t ->
                    t.copy(
                        id = UUID.randomUUID().toString(),
                        blockId = newBlock.id,
                        isCarriedOver = true,
                        isDone = false,
                        sortOrder = i
                    )
                }
                taskDao.insertAll(carried)
            }
        }

        // Re-schedule for next Sunday noon
        BlockScheduler.schedule(applicationContext)
        return Result.success()
    }
}
