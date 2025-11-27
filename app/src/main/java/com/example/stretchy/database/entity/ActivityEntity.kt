package com.example.stretchy.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.stretchy.database.data.ActivityType

/**
 * @deprecated Old structure from version 1-4. Table dropped in MIGRATION_4_5.
 * Use WorkoutEntity instead (version 5+).
 */
@Deprecated(
    message = "Old structure - use WorkoutEntity instead",
    replaceWith = ReplaceWith("WorkoutEntity"),
    level = DeprecationLevel.WARNING
)
@Entity(
    tableName = "activity",
    indices = [Index(value = ["name", "duration"], unique = true)]
)
data class ActivityEntity(
    @PrimaryKey
    val activityId: Long,
    val name: String,
    val duration: Int,
    val activityType: ActivityType
)