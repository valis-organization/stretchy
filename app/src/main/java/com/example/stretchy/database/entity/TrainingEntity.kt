package com.example.stretchy.database.entity

import androidx.room.*
import com.example.stretchy.database.data.TrainingType

@Entity(tableName = "training")
data class TrainingEntity(
    @PrimaryKey val trainingId: Long,
    val name: String,
    val trainingType: TrainingType,
    val finished: Boolean
)
