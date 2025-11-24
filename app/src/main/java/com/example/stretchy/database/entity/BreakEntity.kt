package com.example.stretchy.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breaks",
    indices = [Index(value = ["duration"], unique = false)]
)
data class BreakEntity(
    @PrimaryKey(autoGenerate = true)
    val breakId: Long = 0,
    val duration: Int // 0 = timeless break, >0 = timed break in seconds
)
