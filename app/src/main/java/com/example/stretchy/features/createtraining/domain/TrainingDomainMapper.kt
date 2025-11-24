package com.example.stretchy.features.createtraining.domain

import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.TrainingWithActivity
import com.example.stretchy.database.data.ActivityType

/**
 * Domain Mappers - Clean conversion between UI, Domain, and Repository layers
 * Maintains backward compatibility while introducing clean domain layer
 */
object TrainingDomainMapper {

    // ========= UI ↔ DOMAIN CONVERSIONS =========

    /**
     * Converts UI ExercisesWithBreaks list to Domain models
     */
    fun List<ExercisesWithBreaks>.toDomain(): List<ExerciseWithBreakDomain> {
        return this.map { uiModel ->
            val exerciseDomain = ExerciseDomain(
                id = uiModel.exercise.id?.toLong(),
                name = uiModel.exercise.name,
                duration = uiModel.exercise.duration,
                order = uiModel.listId
            )

            val breakDomain = BreakDomain.fromDuration(uiModel.nextBreakDuration)

            ExerciseWithBreakDomain(exerciseDomain, breakDomain)
        }
    }

    /**
     * Converts Domain models to UI ExercisesWithBreaks list
     */
    fun List<ExerciseWithBreakDomain>.toUI(): List<ExercisesWithBreaks> {
        return this.mapIndexed { index, domainModel ->
            ExercisesWithBreaks(
                listId = index,
                exercise = Exercise(
                    id = domainModel.exercise.id?.toInt(),
                    name = domainModel.exercise.name,
                    duration = domainModel.exercise.duration,
                    activityOrder = domainModel.exercise.order
                ),
                nextBreakDuration = domainModel.breakAfter?.duration,
                isExpanded = false
            )
        }
    }

    /**
     * Converts single ExercisesWithBreaks to Domain
     */
    fun ExercisesWithBreaks.toDomain(): ExerciseWithBreakDomain {
        val exerciseDomain = ExerciseDomain(
            id = exercise.id?.toLong(),
            name = exercise.name,
            duration = exercise.duration,
            order = listId
        )

        val breakDomain = BreakDomain.fromDuration(nextBreakDuration)

        return ExerciseWithBreakDomain(exerciseDomain, breakDomain)
    }

    /**
     * Converts single Domain model to UI
     */
    fun ExerciseWithBreakDomain.toUI(listId: Int = 0): ExercisesWithBreaks {
        return ExercisesWithBreaks(
            listId = listId,
            exercise = Exercise(
                id = exercise.id?.toInt(),
                name = exercise.name,
                duration = exercise.duration,
                activityOrder = exercise.order
            ),
            nextBreakDuration = breakAfter?.duration,
            isExpanded = false
        )
    }

    // ========= DOMAIN ↔ REPOSITORY CONVERSIONS =========

    /**
     * Converts Domain TrainingDomain to Repository TrainingWithActivity
     */
    fun TrainingDomain.toRepository(): TrainingWithActivity {
        val activities = mutableListOf<Activity>()

        exercisesWithBreaks.forEachIndexed { index, exerciseWithBreak ->
            // Add the main activity
            val activity = Activity(
                name = exerciseWithBreak.exercise.name,
                activityOrder = index,
                duration = exerciseWithBreak.exercise.duration,
                activityType = when {
                    exerciseWithBreak.exercise.duration == 0 -> ActivityType.HOLD
                    else -> ActivityType.STRETCH // Default based on training type
                }
            ).apply {
                activityId = exerciseWithBreak.exercise.id ?: 0
            }
            activities.add(activity)

            // Add break activity if exists (for repository compatibility)
            if (exerciseWithBreak.breakAfter != null) {
                val breakActivity = Activity(
                    name = "",
                    activityOrder = index + 1000, // High number for sorting
                    duration = exerciseWithBreak.breakAfter.duration,
                    activityType = ActivityType.BREAK
                ).apply {
                    activityId = 0 // Breaks don't have real activity IDs
                }
                activities.add(breakActivity)
            }
        }

        return TrainingWithActivity(
            name = name,
            trainingType = trainingType,
            finished = isFinished,
            activities = activities
        ).apply {
            this.id = this@toRepository.id ?: 0
        }
    }

    /**
     * Converts Repository TrainingWithActivity to Domain TrainingDomain
     */
    fun TrainingWithActivity.toDomain(): TrainingDomain {
        val exercisesWithBreaks = mutableListOf<ExerciseWithBreakDomain>()

        // Group activities - separate exercises from breaks
        val exercises = activities.filter { it.activityType != ActivityType.BREAK }
            .sortedBy { it.activityOrder }

        exercises.forEach { exercise ->
            val exerciseDomain = ExerciseDomain(
                id = exercise.activityId,
                name = exercise.name,
                duration = exercise.duration,
                order = exercise.activityOrder ?: 0
            )

            // Find break after this exercise
            val nextBreakActivity = activities.find {
                it.activityType == ActivityType.BREAK &&
                it.activityOrder != null &&
                exercise.activityOrder != null &&
                it.activityOrder!! > exercise.activityOrder!!
            }

            val breakDomain = nextBreakActivity?.let { breakActivity ->
                BreakDomain(
                    id = null, // Break activities don't have real IDs in old system
                    duration = breakActivity.duration
                )
            }

            exercisesWithBreaks.add(ExerciseWithBreakDomain(exerciseDomain, breakDomain))
        }

        return TrainingDomain(
            id = id,
            name = name,
            exercisesWithBreaks = exercisesWithBreaks,
            trainingType = trainingType,
            isFinished = finished
        )
    }

    // ========= HELPER FUNCTIONS =========

    /**
     * Updates UI model with domain changes while preserving UI state
     */
    fun ExercisesWithBreaks.updateFromDomain(domain: ExerciseWithBreakDomain): ExercisesWithBreaks {
        return copy(
            exercise = exercise.copy(
                name = domain.exercise.name,
                duration = domain.exercise.duration
            ),
            nextBreakDuration = domain.breakAfter?.duration
        )
    }

    /**
     * Validates domain model before UI conversion
     */
    fun validateBeforeUIConversion(domain: List<ExerciseWithBreakDomain>): List<String> {
        val errors = mutableListOf<String>()

        domain.forEachIndexed { index, exerciseWithBreak ->
            if (!exerciseWithBreak.isValid()) {
                errors.add("Exercise at position ${index + 1} is invalid")
            }
        }

        return errors
    }

    /**
     * Creates domain model from UI with validation
     */
    fun createDomainFromUI(
        name: String,
        exercisesWithBreaks: List<ExercisesWithBreaks>,
        trainingType: com.example.stretchy.database.data.TrainingType
    ): TrainingDomain {
        return TrainingDomain(
            name = name,
            exercisesWithBreaks = exercisesWithBreaks.toDomain(),
            trainingType = trainingType
        )
    }
}

// ========= EXTENSION FUNCTIONS FOR CONVENIENCE =========

/**
 * Extension function for easy conversion from UI to Domain
 */
fun List<ExercisesWithBreaks>.toDomainModels(): List<ExerciseWithBreakDomain> =
    TrainingDomainMapper.run { toDomain() }

/**
 * Extension function for easy conversion from Domain to UI
 */
fun List<ExerciseWithBreakDomain>.toUIModels(): List<ExercisesWithBreaks> =
    TrainingDomainMapper.run { toUI() }

/**
 * Extension function for TrainingWithActivity to Domain conversion
 */
fun TrainingWithActivity.toDomainModel(): TrainingDomain =
    TrainingDomainMapper.run { toDomain() }

/**
 * Extension function for TrainingDomain to Repository conversion
 */
fun TrainingDomain.toRepositoryModel(): TrainingWithActivity =
    TrainingDomainMapper.run { toRepository() }
