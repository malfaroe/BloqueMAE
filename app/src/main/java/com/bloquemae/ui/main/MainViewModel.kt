package com.bloquemae.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.bloquemae.data.AppDatabase
import com.bloquemae.data.Block
import com.bloquemae.data.BlockStatus
import com.bloquemae.data.Task
import com.bloquemae.util.WeekUtils
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    private val blockDao = db.blockDao()
    private val taskDao = db.taskDao()

    val activeBlock: LiveData<Block?> = blockDao.activeBlock().asLiveData()

    val tasks: LiveData<List<Task>> = blockDao.activeBlock()
        .flatMapLatest { block ->
            if (block != null) taskDao.tasksForBlock(block.id) else flowOf(emptyList())
        }
        .asLiveData()

    init {
        viewModelScope.launch { ensureActiveBlock() }
    }

    private suspend fun ensureActiveBlock() {
        val active = blockDao.activeBlockOnce()
        when {
            active == null -> createNewBlock()
            // Phone was off on Sunday — close expired block now
            WeekUtils.isBlockExpired(active.weekEnd) -> closeAndCarryOver(active)
        }
    }

    private suspend fun closeAndCarryOver(block: Block) {
        val total = taskDao.count(block.id)
        val done = taskDao.doneCount(block.id)
        val pct = if (total > 0) done.toFloat() / total else 0f
        blockDao.update(block.copy(status = BlockStatus.CLOSED, completionPct = pct))

        val undone = taskDao.undoneTasks(block.id)
        createNewBlock()
        val newBlock = blockDao.activeBlockOnce() ?: return
        if (undone.isNotEmpty()) {
            taskDao.insertAll(undone.mapIndexed { i, t ->
                t.copy(id = UUID.randomUUID().toString(), blockId = newBlock.id,
                    isCarriedOver = true, isDone = false, sortOrder = i)
            })
        }
    }

    private suspend fun createNewBlock() {
        val last = blockDao.latestBlock()
        blockDao.insert(
            Block(
                number = (last?.number ?: 0) + 1,
                weekStart = WeekUtils.currentWeekStart(),
                weekEnd = WeekUtils.currentWeekEnd()
            )
        )
    }

    fun closeBlockAndCreateNew() {
        viewModelScope.launch {
            val active = blockDao.activeBlockOnce() ?: return@launch
            closeAndCarryOver(active)
        }
    }

    fun addTask(text: String) {
        viewModelScope.launch {
            val block = blockDao.activeBlockOnce() ?: return@launch
            val order = taskDao.count(block.id)
            taskDao.insert(Task(blockId = block.id, text = text, sortOrder = order))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch { taskDao.update(task.copy(isDone = !task.isDone)) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { taskDao.delete(task) }
    }

    fun updateCompletionPct() {
        viewModelScope.launch {
            val block = blockDao.activeBlockOnce() ?: return@launch
            val total = taskDao.count(block.id)
            val done = taskDao.doneCount(block.id)
            val pct = if (total > 0) done.toFloat() / total else 0f
            blockDao.update(block.copy(completionPct = pct))
        }
    }
}
