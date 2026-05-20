package com.bloquemae.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE blockId = :blockId ORDER BY sortOrder ASC, createdAt ASC")
    fun tasksForBlock(blockId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE blockId = :blockId AND isDone = 0")
    suspend fun undoneTasks(blockId: String): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT COUNT(*) FROM tasks WHERE blockId = :blockId")
    suspend fun count(blockId: String): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE blockId = :blockId AND isDone = 1")
    suspend fun doneCount(blockId: String): Int
}
