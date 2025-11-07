package com.example.stretchy.features.executetraining.migration

import com.example.stretchy.features.executetraining.ui.data.*
import com.example.stretchy.repository.TrainingWithActivity
import com.example.stretchy.database.data.ActivityType

/**
 * Migration adapter for gradual transition from old system to new system.
 * This is a placeholder implementation that will be activated during migration.
 */
class ExecuteTrainingMigrationAdapter {
    
    /**
     * Placeholder method for training data conversion.
     * Returns a simple string representation until migration is complete.
     */
    fun convertToTrainingSequence(
        trainingWithActivity: TrainingWithActivity
    ): String {
        return "TrainingSequence(id=${trainingWithActivity.training.trainingId}, " +
               "activities=${trainingWithActivity.activities.size})"
    }
    
    /**
     * Placeholder method for legacy format conversion.
     * Returns empty list until migration is complete.
     */
    fun convertToLegacyFormat(
        trainingSequenceData: String
    ): List<ActivityItemExerciseAndBreakMerged> {
        return emptyList()
    }
    
    /**
     * Helper method to extract activity types from training.
     */
    fun extractActivityTypes(trainingWithActivity: TrainingWithActivity): List<ActivityType> {
        return trainingWithActivity.activities.map { it.activityType }
    }
}
