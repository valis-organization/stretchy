package com.example.stretchy.features.createtraining.domain

import android.util.Log
import com.example.stretchy.repository.Repository

private const val DOMAIN_TAG = "DOMAIN_BREAK"

/**
 * Comprehensive Break Management Use Case
 * Single point for all break-related operations with clean domain logic
 * Wraps repository break operations with domain validation and business rules
 */
class BreakManagementUseCase(private val repository: Repository) {

    /**
     * Creates or finds existing break with given duration
     * @param duration Break duration (0 = timeless, >0 = timed in seconds)
     * @return BreakDomain with proper validation
     * Note: Breaks are now WorkoutEntity with type=BREAK, ID is workout ID
     */
    suspend fun findOrCreateBreak(duration: Int): BreakDomain {
        Log.d(DOMAIN_TAG, "Finding or creating break with duration=$duration")

        require(duration >= 0) { "Break duration cannot be negative: $duration" }

        val workoutId = repository.findOrCreateBreakWorkout(duration)
        return BreakDomain(
            id = workoutId,
            duration = duration,
            isActive = true
        )
    }

    /**
     * Smart break editing with automatic sharing logic
     * - If no duration: remove break
     * - Otherwise: find or create break workout with new duration
     * @param currentBreakId Current break workout ID (null if no break)
     * @param newDuration New duration (null to remove break, 0 = timeless, >0 = timed)
     * @return New break workout ID or null if removed
     * Note: Breaks are now WorkoutEntity with type=BREAK, so we just find/create workout
     */
    suspend fun editBreakSmart(currentBreakId: Long?, newDuration: Int?): BreakDomain? {
        Log.d(DOMAIN_TAG, "Smart break edit: currentId=$currentBreakId, newDuration=$newDuration")

        // Validate new duration if provided
        if (newDuration != null) {
            require(newDuration >= 0) { "Break duration cannot be negative: $newDuration" }
        }

        // If no new duration, return null (no break)
        if (newDuration == null) {
            return null
        }

        // Find or create workout for this break duration
        val newWorkoutId = repository.findOrCreateBreakWorkout(newDuration)

        return BreakDomain(id = newWorkoutId, duration = newDuration, isActive = true)
    }

    /**
     * Deletes break if it's not used by any exercises
     * @param breakId Break workout ID to delete
     * @return true if deleted, false if still in use
     * Note: With new structure, orphaned workouts are automatically cleaned up
     * This method is kept for compatibility but does nothing
     */
    suspend fun deleteBreakIfUnused(breakId: Long): Boolean {
        Log.d(DOMAIN_TAG, "deleteBreakIfUnused is deprecated - workouts are auto-cleaned")
        // Workouts (including breaks) are automatically cleaned up by cleanupOrphanedWorkouts()
        return false
    }

    /**
     * Creates break domain model from duration with validation
     * @param duration Duration in seconds (0 = timeless, >0 = timed)
     * @return BreakDomain or null if invalid
     */
    fun createBreakDomain(duration: Int): BreakDomain? {
        return BreakDomain.fromDuration(duration)
    }

    /**
     * Creates break domain model from UI duration with validation
     * @param uiDuration UI duration (null = no break, 0 = timeless, >0 = timed)
     * @return BreakDomain or null if no break needed
     */
    fun createBreakFromUI(uiDuration: Int?): BreakDomain? {
        return BreakDomain.fromDuration(uiDuration)
    }

    /**
     * Validates break compatibility with exercise using domain rules
     * @param breakDomain Break to validate
     * @param exercise Exercise to check compatibility with
     * @return true if compatible
     */
    fun isBreakCompatibleWithExercise(breakDomain: BreakDomain?, exercise: ExerciseDomain): Boolean {
        return TrainingDomainRules.isBreakCompatibleWithExercise(breakDomain, exercise)
    }

    /**
     * Applies automatic break logic based on preferences
     * @param exercise Exercise to apply break to
     * @param autoBreakDuration Default break duration from preferences
     * @return ExerciseWithBreakDomain with applied break
     */
    fun applyAutomaticBreak(exercise: ExerciseDomain, autoBreakDuration: Int?): ExerciseWithBreakDomain {
        Log.d(DOMAIN_TAG, "Applying automatic break: exercise=${exercise.name}, autoBreak=$autoBreakDuration")

        val breakDomain = if (autoBreakDuration != null && autoBreakDuration > 0) {
            createBreakDomain(autoBreakDuration)
        } else {
            null
        }

        return ExerciseWithBreakDomain(exercise, breakDomain)
    }

    /**
     * Updates break for specific exercise with validation
     * @param exerciseWithBreak Current exercise with break
     * @param newBreakDuration New break duration (null = no break)
     * @return Updated ExerciseWithBreakDomain
     */
    suspend fun updateExerciseBreak(
        exerciseWithBreak: ExerciseWithBreakDomain,
        newBreakDuration: Int?
    ): ExerciseWithBreakDomain {
        Log.d(DOMAIN_TAG, "Updating exercise break: ${exerciseWithBreak.exercise.name}, newDuration=$newBreakDuration")

        val newBreak = editBreakSmart(exerciseWithBreak.breakAfter?.id, newBreakDuration)

        // Validate compatibility
        if (!isBreakCompatibleWithExercise(newBreak, exerciseWithBreak.exercise)) {
            Log.w(DOMAIN_TAG, "Break incompatible with exercise, proceeding anyway")
        }

        return exerciseWithBreak.withBreak(newBreak)
    }

    /**
     * Gets break usage statistics for UI display
     * @param breakId Break ID to check
     * @return Usage information
     */
    suspend fun getBreakUsageInfo(breakId: Long): BreakUsageInfo {
        // This would require additional repository method to get usage details
        // For now, return basic info
        return BreakUsageInfo(breakId, usageCount = 1, isShared = false)
    }
}

/**
 * Break usage information for UI display
 */
data class BreakUsageInfo(
    val breakId: Long,
    val usageCount: Int,
    val isShared: Boolean
) {
    fun getDisplayText(): String = when {
        usageCount == 0 -> "Unused"
        usageCount == 1 -> "Used by 1 exercise"
        else -> "Shared by $usageCount exercises"
    }
}

// ========= DOMAIN MAPPERS - Clean conversion between layers =========
// Note: BreakEntity mappers removed - breaks are now WorkoutEntity with type=BREAK
// BreakDomain now uses workout ID directly
