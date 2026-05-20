package com.bloquemae.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.bloquemae.data.*
import com.bloquemae.util.WeekUtils
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

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
        if (blockDao.activeBlockOnce() == null) createNewBlock()
    }

    suspend fun createNewBlock() {
        val last = blockDao.latestBlock()
        val nextNumber = (last?.number ?: 0) + 1
        blockDao.insert(
            Block(
                number = nextNumber,
                weekStart = WeekUtils.currentWeekStart(),
                weekEnd = WeekUtils.currentWeekEnd()
            )
        )
    }

    fun closeBlockAndCreateNew() {
        viewModelScope.launch {
            val active = blockDao.activeBlockOnce() ?: return@launch
            val total = taskDao.count(active.id)
            val done = taskDao.doneCount(active.id)
            val pct = if (total > 0) done.toFloat() / total else 0f
            blockDao.update(active.copy(status = BlockStatus.CLOSED, completionPct = pct))

            val undone = taskDao.undoneTasks(active.id)
            createNewBlock()
            val newBlock = blockDao.activeBlockOnce() ?: return@launch
            val carriedTasks = undone.mapIndexed { i, t ->
                t.copy(id = java.util.UUID.randomUUID().toString(), blockId = newBlock.id, isCarriedOver = true, isDone = false, sortOrder = i)
            }
            taskDao.insertAll(carriedTasks)
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
