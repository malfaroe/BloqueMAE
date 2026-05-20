package com.bloquemae.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks ORDER BY number DESC")
    fun allBlocks(): Flow<List<Block>>

    @Query("SELECT * FROM blocks WHERE status = 'ACTIVE' LIMIT 1")
    fun activeBlock(): Flow<Block?>

    @Query("SELECT * FROM blocks WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun activeBlockOnce(): Block?

    @Query("SELECT * FROM blocks ORDER BY number DESC LIMIT 1")
    suspend fun latestBlock(): Block?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: Block)

    @Update
    suspend fun update(block: Block)

    @Query("""
        SELECT b.id, b.number, b.weekStart, b.weekEnd, b.status, b.completionPct, b.createdAt,
               COUNT(t.id) AS totalTasks,
               SUM(CASE WHEN t.isDone = 1 THEN 1 ELSE 0 END) AS doneTasks
        FROM blocks b
        LEFT JOIN tasks t ON t.blockId = b.id
        WHERE b.status = 'CLOSED'
        GROUP BY b.id
        ORDER BY b.number DESC
    """)
    fun closedBlocksWithStats(): Flow<List<BlockWithStats>>
}
