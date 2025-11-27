package com.example.stretchy.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stretchy.database.data.WorkoutType

@Entity(tableName = "workout")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val workoutId: Long = 0,
    val name: String,
    val durationSeconds: Int,
    val workoutType: WorkoutType
)

