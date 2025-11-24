package com.example.stretchy.features.createtraining.domain

import com.example.stretchy.features.createtraining.domain.TrainingDomainMapper.toDomain
import com.example.stretchy.features.createtraining.domain.TrainingDomainMapper.toUI
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.Exercise

/**
 * Usage Examples - Clean Architecture Implementation
 * Demonstrates how to use the new domain layer for break management
 */

/**
 * Example 1: Creating Training with Domain Models
 */
fun createTrainingExample(): TrainingDomain {
    // Create exercises using domain models
    val pushUpExercise = ExerciseDomain(
        name = "Push Ups",
        duration = 30,
        order = 0
    )

    val plankExercise = ExerciseDomain(
        name = "Plank",
        duration = 60,
        order = 1
    )

    // Create breaks using domain models
    val shortBreak = BreakDomain.createTimed(15) // 15 seconds
    val longBreak = BreakDomain.createTimed(30)  // 30 seconds

    // Combine exercises with breaks
    val exercisesWithBreaks = listOf(
        ExerciseWithBreakDomain(pushUpExercise, shortBreak),
        ExerciseWithBreakDomain(plankExercise, longBreak)
    )

    // Create complete training
    return TrainingDomain(
        name = "Morning Workout",
        exercisesWithBreaks = exercisesWithBreaks,
        trainingType = DomainTrainingType.STRETCH
    )
}

/**
 * Example 2: Converting Between UI and Domain Layers
 */
fun uiToDomainConversionExample(uiExerciseList: List<ExercisesWithBreaks>): List<ExerciseWithBreakDomain> {
    // Convert UI models to domain models
    return TrainingDomainMapper.run {
        uiExerciseList.toDomain()
    }
}

fun domainToUIConversionExample(domainList: List<ExerciseWithBreakDomain>): List<ExercisesWithBreaks> {
    // Convert domain models back to UI models
    return TrainingDomainMapper.run {
        domainList.toUI()
    }
}

/**
 * Example 3: Break Management Use Case Usage
 */
suspend fun breakManagementExample(breakUseCase: BreakManagementUseCase) {
    // Create a new break
    val newBreak = breakUseCase.findOrCreateBreak(duration = 20)
    println("Created break: ${newBreak.getDisplayText()}")

    // Create an exercise and apply automatic break
    val exercise = ExerciseDomain(name = "Squats", duration = 45, order = 0)
    val exerciseWithAutoBreak = breakUseCase.applyAutomaticBreak(exercise, autoBreakDuration = 15)

    // Validate break compatibility
    val isCompatible = breakUseCase.isBreakCompatibleWithExercise(
        exerciseWithAutoBreak.breakAfter,
        exercise
    )
    println("Break compatibility: $isCompatible")

    // Smart break editing (will reuse existing break or create new one)
    val updatedBreak = breakUseCase.editBreakSmart(
        currentBreakId = newBreak.id,
        newDuration = 25
    )
    println("Updated break: ${updatedBreak?.getDisplayText() ?: "No break"}")
}

/**
 * Example 4: Domain Validation Rules
 */
fun validationExample(): List<String> {
    val training = TrainingDomain(
        name = "", // Invalid - empty name
        exercisesWithBreaks = listOf(
            ExerciseWithBreakDomain(
                exercise = ExerciseDomain(name = "", duration = -1, order = 0), // Invalid exercise
                breakAfter = BreakDomain(duration = 0) // Timeless break after invalid exercise
            )
        ),
        trainingType = DomainTrainingType.STRETCH
    )

    // Validate using domain rules
    return TrainingDomainRules.validateTraining(training)
}

/**
 * Example 5: Advanced Break Operations
 */
fun advancedBreakExample(): ExerciseWithBreakDomain {
    // Create exercise
    val exercise = ExerciseDomain(name = "Burpees", duration = 0, order = 0) // Timeless exercise

    // Try different break types
    val timedBreak = BreakDomain.createTimed(30)
    val timelessBreak = BreakDomain.createTimeless()

    // Create exercise with break combinations
    val exerciseWithTimedBreak = ExerciseWithBreakDomain(exercise, timedBreak)
    val exerciseWithTimelessBreak = ExerciseWithBreakDomain(exercise, timelessBreak)

    // Use domain validation to check compatibility
    val timedCompatible = TrainingDomainRules.isBreakCompatibleWithExercise(timedBreak, exercise)
    val timelessCompatible = TrainingDomainRules.isBreakCompatibleWithExercise(timelessBreak, exercise)

    println("Timed break with timeless exercise: $timedCompatible")        // Should be true
    println("Timeless break with timeless exercise: $timelessCompatible") // Should be false

    return if (timedCompatible) exerciseWithTimedBreak else ExerciseWithBreakDomain(exercise, null)
}

/**
 * Example 6: ViewModel Integration Pattern
 */
class ExampleViewModelUsage(
    private val breakManagementUseCase: BreakManagementUseCase
) {

    suspend fun updateBreakForExercise(exerciseIndex: Int, newDuration: Int?) {
        try {
            // Get current exercise from UI state
            val currentExercise = getCurrentExerciseFromUI(exerciseIndex)

            // Convert to domain
            val domainExercise = currentExercise.toDomain()

            // Use domain use case for break update
            val updatedExercise = breakManagementUseCase.updateExerciseBreak(
                domainExercise,
                newDuration
            )

            // Convert back to UI and update state
            val updatedUI = updatedExercise.toUI(exerciseIndex)
            updateUIState(exerciseIndex, updatedUI)

        } catch (e: Exception) {
            handleError("Failed to update break: ${e.message}")
        }
    }

    fun validateCurrentTraining(): List<String> {
        val currentUI = getCurrentUIState()
        val domainTraining = TrainingDomainMapper.createDomainFromUI(
            name = currentUI.name,
            exercisesWithBreaks = currentUI.exercises,
            trainingType = currentUI.type
        )

        return TrainingDomainRules.validateTraining(domainTraining)
    }

    // Mock methods for example
    private fun getCurrentExerciseFromUI(index: Int): ExercisesWithBreaks =
        ExercisesWithBreaks(0, Exercise(), null, false)

    private fun updateUIState(index: Int, exercise: ExercisesWithBreaks) {}
    private fun handleError(message: String) {}
    private fun getCurrentUIState() = object {
        val name = "Test Training"
        val exercises = emptyList<ExercisesWithBreaks>()
        val type = com.example.stretchy.database.data.TrainingType.STRETCH
    }
}

/**
 * Example 7: Testing Domain Models (Unit Test Example)
 */
fun testBreakDomainLogic() {
    // Test break creation
    assert(BreakDomain.createTimed(30)?.duration == 30)
    assert(BreakDomain.createTimed(-1) == null) // Invalid duration
    assert(BreakDomain.createTimeless().isTimeless())

    // Test break display text
    assert(BreakDomain.createTimed(1)?.getDisplayText() == "1 second break")
    assert(BreakDomain.createTimed(30)?.getDisplayText() == "30 seconds break")
    assert(BreakDomain.createTimeless().getDisplayText() == "Break until you continue")

    // Test exercise with break combinations
    val exercise = ExerciseDomain(name = "Test", duration = 30, order = 0)
    val breakDomain = BreakDomain.createTimed(15)
    val combined = ExerciseWithBreakDomain(exercise, breakDomain)

    assert(combined.getTotalDuration() == 45) // 30 + 15
    assert(combined.hasBreak())
    assert(combined.hasTimedBreak())
    assert(!combined.hasTimelessBreak())

    println("All domain model tests passed!")
}
