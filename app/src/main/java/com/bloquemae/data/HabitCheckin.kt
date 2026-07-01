package com.bloquemae.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "habit_checkins",
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("habitId"), Index(value = ["habitId", "date"], unique = true)]
)
data class HabitCheckin(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: Long,     // epoch ms, normalized to local midnight of the check-in day
    val done: Boolean
)
