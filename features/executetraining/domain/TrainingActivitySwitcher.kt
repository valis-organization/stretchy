package com.example.stretchy.features.executetraining.domain

/**
 * Central system for managing training progression
 * Handles both automatic (timer-based) and manual (user-click) navigation
 */
class TrainingActivitySwitcher(
    private val trainingSequence: TrainingSequence
) {

    sealed class SwitchMode {
        object AutomaticProgression : SwitchMode() // Timer ended, follow normal flow (show the break after that)
        object UserSkipToNext : SwitchMode() // User clicked next exercise, skip the break entirely and start from the new exercise
        object UserSkipToPrevious : SwitchMode() // User clicked previous exercise
        object UserSkipBreak : SwitchMode() // User wants to skip current break
    }

    sealed class SwitchResult {
        data class MoveToExercise(val exercise: ExerciseItem) : SwitchResult()
        data class MoveToBreak(val break: BreakItem) : SwitchResult()
        object TrainingCompleted : SwitchResult()
        object NoActionNeeded : SwitchResult()
    }

    /**
     * Main switching logic - decides what should happen next
     */
    fun switchToNext(
        currentItem: TrainingSequenceItem,
        mode: SwitchMode
    ): SwitchResult {
        return when (mode) {
            SwitchMode.AutomaticProgression -> handleAutomaticProgression(currentItem)
            SwitchMode.UserSkipToNext -> handleUserSkipToNext(currentItem)
            SwitchMode.UserSkipToPrevious -> handleUserSkipToPrevious(currentItem)
            SwitchMode.UserSkipBreak -> handleUserSkipBreak(currentItem)
        }
    }

    private fun handleAutomaticProgression(currentItem: TrainingSequenceItem): SwitchResult {
        val nextItem = trainingSequence.getNextItem(currentItem.order)

        return when {
            nextItem == null -> SwitchResult.TrainingCompleted
            nextItem is ExerciseItem -> SwitchResult.MoveToExercise(nextItem)
            nextItem is BreakItem -> SwitchResult.MoveToBreak(nextItem)
            else -> SwitchResult.NoActionNeeded
        }
    }

    private fun handleUserSkipToNext(currentItem: TrainingSequenceItem): SwitchResult {
        // User navigation always skips to next exercise (not break)
        val nextExercise = trainingSequence.getNextExercise(currentItem.order)

        return when {
            nextExercise == null -> SwitchResult.TrainingCompleted
            else -> SwitchResult.MoveToExercise(nextExercise)
        }
    }

    private fun handleUserSkipToPrevious(currentItem: TrainingSequenceItem): SwitchResult {
        // User navigation always skips to previous exercise (not break)
        val previousExercise = trainingSequence.getPreviousExercise(currentItem.order)

        return when {
            previousExercise == null -> SwitchResult.NoActionNeeded
            else -> SwitchResult.MoveToExercise(previousExercise)
        }
    }

    private fun handleUserSkipBreak(currentItem: TrainingSequenceItem): SwitchResult {
        // Skip current break and go to next exercise
        if (currentItem !is BreakItem) return SwitchResult.NoActionNeeded

        val nextExercise = trainingSequence.getNextExercise(currentItem.order)
        return when {
            nextExercise == null -> SwitchResult.TrainingCompleted
            else -> SwitchResult.MoveToExercise(nextExercise)
        }
    }

    /**
     * Check if user can navigate to next exercise
     */
    fun canNavigateToNext(currentItem: TrainingSequenceItem): Boolean {
        return trainingSequence.getNextExercise(currentItem.order) != null
    }

    /**
     * Check if user can navigate to previous exercise
     */
    fun canNavigateToPrevious(currentItem: TrainingSequenceItem): Boolean {
        return trainingSequence.getPreviousExercise(currentItem.order) != null
    }
}
