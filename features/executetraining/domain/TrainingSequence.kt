package com.example.stretchy.features.executetraining.domain

import com.example.stretchy.database.data.ActivityType

/**
 * Decoupled training sequence model
 * Clean separation between exercises and breaks
 */
sealed class TrainingSequenceItem {
    abstract val order: Int
    abstract val duration: Int // in seconds
}

data class ExerciseItem(
    override val order: Int,
    override val duration: Int,
    val exerciseId: Long,
    val name: String,
    val activityType: ActivityType
) : TrainingSequenceItem()

data class BreakItem(
    override val order: Int,
    override val duration: Int,
    val breakId: String,
    val beforeExercise: String // name of exercise this break follows
) : TrainingSequenceItem()

/**
 * Complete training sequence with exercises and optional breaks
 */
data class TrainingSequence(
    val trainingId: Long,
    val items: List<TrainingSequenceItem>
) {
    /**
     * Get next exercise (skipping breaks) for navigation
     */
    fun getNextExercise(currentOrder: Int): ExerciseItem? {
        return items.filterIsInstance<ExerciseItem>()
            .firstOrNull { it.order > currentOrder }
    }

    /**
     * Get previous exercise (skipping breaks) for navigation
     */
    fun getPreviousExercise(currentOrder: Int): ExerciseItem? {
        return items.filterIsInstance<ExerciseItem>()
            .lastOrNull { it.order < currentOrder }
    }

    /**
     * Get next item in sequence (could be exercise or break)
     */
    fun getNextItem(currentOrder: Int): TrainingSequenceItem? {
        return items.firstOrNull { it.order > currentOrder }
    }

    /**
     * Check if there's a break after current exercise
     */
    fun hasBreakAfter(exerciseOrder: Int): Boolean {
        val nextItem = getNextItem(exerciseOrder)
        return nextItem is BreakItem
    }
}
