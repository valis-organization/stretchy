package com.example.stretchy.database.entity

import androidx.room.*
import com.example.stretchy.database.data.TrainingType

@Entity(tableName = "training")
data class TrainingEntity(
    @PrimaryKey val trainingId: Long,
    val name: String,
    val trainingType: TrainingType,
    val isDraft: Boolean? = null, // null = finished/saved training, true = draft
    val sequence: String // Comma-separated workout IDs, e.g., "1,2,3,4"
)
