package com.bloquemae.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reviews")
data class DailyReview(
    @PrimaryKey val date: Long,
    val text: String,
    val updatedAt: Long = System.currentTimeMillis()
)
