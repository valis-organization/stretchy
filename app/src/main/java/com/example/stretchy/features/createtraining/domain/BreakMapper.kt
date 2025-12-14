package com.example.stretchy.features.createtraining.domain

import android.util.Log

private const val MIG_TAG = "MIG_3"

/**
 * Maps between UI representations and break entities
 */
object BreakMapper {

    /**
     * Maps UI break duration to break entity requirements
     * @param duration UI duration value (null = no break, 0 = timeless, >0 = timed)
     * @return Pair<needsBreak, breakDuration> where needsBreak indicates if break entity is needed
     */
    fun mapUIDurationToBreakRequirement(duration: Int?): Pair<Boolean, Int?> {
        Log.d(MIG_TAG, "Mapping UI duration to break requirement: $duration")

        return when (duration) {
            null -> {
                Log.d(MIG_TAG, "No break needed")
                false to null
            }
            0 -> {
                Log.d(MIG_TAG, "Timeless break needed")
                true to 0
            }
            else -> {
                Log.d(MIG_TAG, "Timed break needed: $duration seconds")
                true to duration
            }
        }
    }

    /**
     * Maps break entity duration back to UI representation
     * @param breakDuration Break entity duration (null = no break, 0 = timeless, >0 = timed)
     * @return UI duration value for ExercisesWithBreaks.nextBreakDuration
     */
    fun mapBreakDurationToUI(breakDuration: Int?): Int? {
        Log.d(MIG_TAG, "Mapping break duration to UI: $breakDuration")

        return when (breakDuration) {
            null -> {
                Log.d(MIG_TAG, "Mapped to no break (null)")
                null
            }
            0 -> {
                Log.d(MIG_TAG, "Mapped to timeless break (0)")
                0
            }
            else -> {
                Log.d(MIG_TAG, "Mapped to timed break: $breakDuration")
                breakDuration
            }
        }
    }

    /**
     * Creates verbose break description for UI consumption
     * @param duration Break duration (null = no break, 0 = timeless, >0 = timed)
     * @return Human-readable description
     */
    fun getBreakDescription(duration: Int?): String {
        return when (duration) {
            null -> "No break"
            0 -> "Break until you continue"
            1 -> "1 second break"
            else -> "$duration seconds break"
        }
    }

    /**
     * Validates if break duration is valid
     * @param duration Duration to validate
     * @return true if valid, false otherwise
     */
    fun isValidBreakDuration(duration: Int?): Boolean {
        return duration == null || duration >= 0
    }
}
