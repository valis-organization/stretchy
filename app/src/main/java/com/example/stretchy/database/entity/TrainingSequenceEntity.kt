package com.example.stretchy.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Links exercises to their following breaks in training sequences
 * Decoupled approach - exercises and breaks are separate concepts
 */
@Entity(
    tableName = "training_sequence",
    primaryKeys = ["trainingId", "sequenceOrder"],
    foreignKeys = [
        ForeignKey(
            entity = TrainingEntity::class,
            parentColumns = ["trainingId"],
            childColumns = ["trainingId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["activityId"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BreakTemplateEntity::class,
            parentColumns = ["breakId"],
            childColumns = ["followingBreakId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["trainingId", "sequenceOrder"]),
        Index(value = ["exerciseId"]),
        Index(value = ["followingBreakId"])
    ]
)
data class TrainingSequenceEntity(
    val trainingId: Long,
    val sequenceOrder: Int, // 0, 1, 2... order in training
    val exerciseId: Long, // FK to ActivityEntity (only EXERCISE/STRETCH/TIMELESS_EXERCISE)
    val followingBreakId: String? = null // FK to BreakTemplateEntity (optional break after this exercise)
)
