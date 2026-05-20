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
}
