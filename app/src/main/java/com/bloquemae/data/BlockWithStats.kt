package com.bloquemae.data

data class BlockWithStats(
    val id: String,
    val number: Int,
    val weekStart: Long,
    val weekEnd: Long,
    val status: BlockStatus,
    val completionPct: Float,
    val createdAt: Long,
    val totalTasks: Int,
    val doneTasks: Int
)
