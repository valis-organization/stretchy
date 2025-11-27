package com.example.stretchy.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @deprecated Old structure from version 1-4. Table dropped in MIGRATION_4_5.
 * Breaks are now WorkoutEntity with workoutType=BREAK (version 5+).
 */
@Deprecated(
    message = "Old structure - use WorkoutEntity with type=BREAK instead",
    replaceWith = ReplaceWith("WorkoutEntity"),
    level = DeprecationLevel.WARNING
)
@Entity(
    tableName = "breaks",
    indices = [Index(value = ["duration"], unique = false)]
)
data class BreakEntity(
    @PrimaryKey(autoGenerate = true)
    val breakId: Long = 0,
    val duration: Int // 0 = timeless break, >0 = timed break in seconds
)
