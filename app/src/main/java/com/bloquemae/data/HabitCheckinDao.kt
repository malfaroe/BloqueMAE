package com.bloquemae.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCheckinDao {
    @Query("SELECT * FROM habit_checkins WHERE date = :date")
    fun checkinsForDate(date: Long): Flow<List<HabitCheckin>>

    @Query("SELECT * FROM habit_checkins WHERE habitId = :habitId AND date BETWEEN :start AND :end")
    suspend fun checkinsForHabitInRange(habitId: String, start: Long, end: Long): List<HabitCheckin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(checkin: HabitCheckin)
}
