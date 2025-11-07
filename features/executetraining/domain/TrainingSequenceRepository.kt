package com.example.stretchy.features.executetraining.domain

import com.example.stretchy.database.dao.BreakTemplateDao
import com.example.stretchy.database.dao.TrainingSequenceDao
import com.example.stretchy.database.entity.BreakTemplateEntity
import com.example.stretchy.database.entity.TrainingSequenceEntity
import java.util.UUID
import javax.inject.Inject

/**
 * Repository for managing breaks and training sequences
 * Handles break reusability and intelligent break management
 */
class TrainingSequenceRepository @Inject constructor(
    private val trainingSequenceDao: TrainingSequenceDao,
    private val breakTemplateDao: BreakTemplateDao
) {

    sealed class BreakEditResult {
        object CanModifyExisting : BreakEditResult()
        object MustCreateNew : BreakEditResult()
        data class UserChoice(val usageCount: Int) : BreakEditResult()
    }

    /**
     * Get complete training sequence with exercises and breaks
     */
    suspend fun getTrainingSequence(trainingId: Long): TrainingSequence {
        val sequenceDetails = trainingSequenceDao.getTrainingSequenceWithDetails(trainingId)

        val items = mutableListOf<TrainingSequenceItem>()

        sequenceDetails.forEach { detail ->
            // Add exercise
            items.add(
                ExerciseItem(
                    order = detail.sequenceOrder * 2, // Even numbers for exercises
                    duration = detail.duration,
                    exerciseId = detail.exerciseId,
                    name = detail.name,
                    activityType = detail.activityType
                )
            )

            // Add break if exists
            if (detail.followingBreakId != null && detail.breakDuration != null) {
                items.add(
                    BreakItem(
                        order = detail.sequenceOrder * 2 + 1, // Odd numbers for breaks
                        duration = detail.breakDuration,
                        breakId = detail.followingBreakId,
                        beforeExercise = detail.name
                    )
                )
            }
        }

        return TrainingSequence(trainingId, items.sortedBy { it.order })
    }

    /**
     * Get or create break template for given duration
     * Handles reusability - reuses existing breaks of same duration
     */
    suspend fun getOrCreateBreakTemplate(duration: Int): BreakTemplateEntity {
        val existing = breakTemplateDao.findBreakByDuration(duration)

        return if (existing != null) {
            // Increment usage count
            breakTemplateDao.updateUsageCount(existing.breakId, 1)
            existing.copy(usageCount = existing.usageCount + 1)
        } else {
            // Create new break template
            val newBreak = BreakTemplateEntity(
                breakId = "break_${duration}s_${UUID.randomUUID()}",
                duration = duration,
                usageCount = 1
            )
            breakTemplateDao.insertBreakTemplate(newBreak)
            newBreak
        }
    }

    /**
     * Check what happens when user wants to edit a break
     */
    suspend fun checkBreakEditStrategy(breakId: String, newDuration: Int): BreakEditResult {
        val breakTemplate = breakTemplateDao.getBreakTemplate(breakId) ?: return BreakEditResult.MustCreateNew

        return when {
            breakTemplate.usageCount <= 1 -> BreakEditResult.CanModifyExisting
            else -> BreakEditResult.UserChoice(breakTemplate.usageCount)
        }
    }

    /**
     * Get popular break templates for UI picker
     */
    suspend fun getPopularBreaks(): List<BreakTemplateEntity> {
        return breakTemplateDao.getAllBreakTemplates().take(10)
    }

    /**
     * Get common break durations for UI suggestions
     * These can be pre-populated or based on usage patterns
     */
    suspend fun getCommonBreakDurations(): List<Int> {
        // Return commonly used break durations
        // This could be based on analytics or pre-defined suggestions
        return listOf(5, 15, 30, 60)
    }
}
