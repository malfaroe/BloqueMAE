package com.bloquemae.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter fun fromStatus(s: BlockStatus): String = s.name
    @TypeConverter fun toStatus(s: String): BlockStatus   = BlockStatus.valueOf(s)
}
