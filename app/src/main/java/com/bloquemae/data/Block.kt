package com.bloquemae.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class BlockStatus { ACTIVE, CLOSED }

@Entity(tableName = "blocks")
data class Block(
    @PrimaryKey val id: String       = UUID.randomUUID().toString(),
    val number: Int,
    val weekStart: Long,             // epoch ms — lunes 00:00
    val weekEnd: Long,               // epoch ms — domingo 23:59
    val status: BlockStatus          = BlockStatus.ACTIVE,
    val completionPct: Float         = 0f,
    val createdAt: Long              = System.currentTimeMillis()
)
