package com.bloquemae.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Block::class,
        parentColumns = ["id"],
        childColumns = ["blockId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("blockId")]
)
data class Task(
    @PrimaryKey val id: String       = UUID.randomUUID().toString(),
    val blockId: String,
    val text: String,
    val isDone: Boolean              = false,
    val isCarriedOver: Boolean       = false,
    val createdAt: Long              = System.currentTimeMillis(),
    val sortOrder: Int               = 0
)
