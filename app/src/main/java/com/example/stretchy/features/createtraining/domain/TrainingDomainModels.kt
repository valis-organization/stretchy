package com.example.stretchy.features.createtraining.domain

import com.example.stretchy.database.data.TrainingType

// ========= CLEAN DOMAIN MODELS - Separated from UI and Data layers =========

/**
 * Clean domain model for Exercise without UI concerns
 */
data class ExerciseDomain(
    val id: Long? = null,
    val name: String,
    val duration: Int, // 0 = timeless exercise
    val order: Int
) {
    fun isValid(): Boolean = name.isNotBlank() && duration >= 0

    companion object {
        fun create(name: String, duration: Int, order: Int, id: Long? = null): ExerciseDomain {
            return ExerciseDomain(id, name, duration, order)
        }
    }
}

/**
 * Clean domain model for Break without UI concerns
 */
data class BreakDomain(
    val id: Long? = null,
    val duration: Int, // 0 = timeless break, >0 = timed break in seconds
    val isActive: Boolean = true
) {
    fun isTimeless(): Boolean = duration == 0
    fun isValid(): Boolean = duration >= 0

    fun getDisplayText(): String = when {
        duration == 0 -> "Break until you continue"
        duration == 1 -> "1 second break"
        else -> "$duration seconds break"
    }

    companion object {
        fun createTimed(duration: Int, id: Long? = null): BreakDomain? {
            return if (duration > 0) BreakDomain(id, duration, true) else null
        }

        fun createTimeless(id: Long? = null): BreakDomain {
            return BreakDomain(id, 0, true)
        }

        fun fromDuration(duration: Int?, id: Long? = null): BreakDomain? {
            return when {
                duration == null -> null
                duration == 0 -> createTimeless(id)
                duration > 0 -> createTimed(duration, id)
                else -> null
            }
        }
    }
}

/**
 * Domain model representing Exercise with its optional Break
 */
data class ExerciseWithBreakDomain(
    val exercise: ExerciseDomain,
    val breakAfter: BreakDomain? = null
) {
    fun isValid(): Boolean = exercise.isValid() && (breakAfter?.isValid() != false)

    fun withBreak(breakDomain: BreakDomain?): ExerciseWithBreakDomain = copy(breakAfter = breakDomain)
    fun withExercise(exercise: ExerciseDomain): ExerciseWithBreakDomain = copy(exercise = exercise)

    fun getTotalDuration(): Int = exercise.duration + (breakAfter?.duration ?: 0)

    fun hasBreak(): Boolean = breakAfter != null
    fun hasTimelessBreak(): Boolean = breakAfter?.isTimeless() == true
    fun hasTimedBreak(): Boolean = breakAfter?.let { !it.isTimeless() } == true
}

/**
 * Domain model for complete Training
 */
data class TrainingDomain(
    val id: Long? = null,
    val name: String,
    val exercisesWithBreaks: List<ExerciseWithBreakDomain>,
    val trainingType: TrainingType,
    val isFinished: Boolean = true
) {
    fun isValid(): Boolean = name.isNotBlank() &&
                            exercisesWithBreaks.isNotEmpty() &&
                            exercisesWithBreaks.all { it.isValid() }

    fun getTotalDuration(): Int = exercisesWithBreaks.sumOf { it.getTotalDuration() }
    fun getExerciseCount(): Int = exercisesWithBreaks.size
    fun getBreaksCount(): Int = exercisesWithBreaks.count { it.hasBreak() }
    fun getTimelessBreaksCount(): Int = exercisesWithBreaks.count { it.hasTimelessBreak() }

    fun withName(name: String): TrainingDomain = copy(name = name)
    fun withExercises(exercises: List<ExerciseWithBreakDomain>): TrainingDomain = copy(exercisesWithBreaks = exercises)

    fun updateExercise(index: Int, exercise: ExerciseWithBreakDomain): TrainingDomain {
        return if (index in exercisesWithBreaks.indices) {
            val updatedList = exercisesWithBreaks.toMutableList()
            updatedList[index] = exercise
            copy(exercisesWithBreaks = updatedList)
        } else this
    }
}

// ========= DOMAIN VALIDATION RULES =========

object TrainingDomainRules {

    /**
     * Validates break compatibility with exercise
     */
    fun isBreakCompatibleWithExercise(breakDomain: BreakDomain?, exercise: ExerciseDomain): Boolean {
        // Business rule: timeless breaks don't make sense after timeless exercises
        if (breakDomain?.isTimeless() == true && exercise.duration == 0) {
            return false // Both are timeless - doesn't make sense
        }
        return breakDomain?.isValid() != false
    }

    /**
     * Validates training structure
     */
    fun validateTraining(training: TrainingDomain): List<String> {
        val errors = mutableListOf<String>()

        if (training.name.isBlank()) {
            errors.add("Training name cannot be empty")
        }

        if (training.exercisesWithBreaks.isEmpty()) {
            errors.add("Training must have at least one exercise")
        }

        training.exercisesWithBreaks.forEachIndexed { index, exerciseWithBreak ->
            if (!exerciseWithBreak.exercise.isValid()) {
                errors.add("Exercise at position ${index + 1} is invalid")
            }

            if (!isBreakCompatibleWithExercise(exerciseWithBreak.breakAfter, exerciseWithBreak.exercise)) {
                errors.add("Break after exercise ${index + 1} is not compatible")
            }
        }

        return errors
    }
}
