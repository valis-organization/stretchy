package com.example.stretchy.database.entity

import androidx.room.Entity

/**
 * @deprecated Old structure from version 1-4. Table dropped in MIGRATION_4_5.
 * Training sequences are now stored as comma-separated workout IDs in TrainingEntity.sequence (version 5+).
 */
@Deprecated(
    message = "Old structure - use TrainingEntity.sequence instead",
    replaceWith = ReplaceWith("TrainingEntity"),
    level = DeprecationLevel.WARNING
)
@Entity(
    tableName = "training_activities",
    primaryKeys = ["tId", "activityOrder"]
)
class TrainingActivityEntity(
    val tId: Long,
    val aId: Long,
    val activityOrder: Int,
    val breakId: Long? = null // Reference to BreakEntity, null = no break after this activity
)