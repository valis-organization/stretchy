package com.example.stretchy.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Reusable break templates - decoupled from specific exercises
 * Allows multiple exercises to share the same break duration
 */
@Entity(
    tableName = "break_templates"
)
data class BreakTemplateEntity(
    @PrimaryKey
    val breakId: String,
    val duration: Int, // in seconds
    val usageCount: Int = 0 // tracks how many places use this break
)
