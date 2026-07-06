package com.bloquemae.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyReviewDao {
    @Query("SELECT * FROM daily_reviews ORDER BY date DESC")
    fun allReviews(): Flow<List<DailyReview>>

    @Query("SELECT * FROM daily_reviews WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: Long): DailyReview?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(review: DailyReview)

    @Delete
    suspend fun delete(review: DailyReview)
}
