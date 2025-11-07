package com.example.stretchy.features.executetraining.repository

import com.example.stretchy.database.migration.MigrationFlags
import com.example.stretchy.features.executetraining.domain.TrainingSequence
import com.example.stretchy.features.executetraining.domain.TrainingSequenceRepository
import com.example.stretchy.features.executetraining.migration.ExecuteTrainingMigrationAdapter
import com.example.stretchy.features.domain.usecases.FetchTrainingByIdUseCase
import com.example.stretchy.repository.TrainingWithActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hybrid repository that can work with both old and new systems
 * Provides seamless transition during migration period
 */
@Singleton
class HybridTrainingRepository @Inject constructor(
    private val migrationFlags: MigrationFlags,
    private val legacyFetchTrainingUseCase: FetchTrainingByIdUseCase,
    private val newTrainingSequenceRepository: TrainingSequenceRepository,
    private val migrationAdapter: ExecuteTrainingMigrationAdapter
) {

    /**
     * Fetch training data using appropriate system based on migration flags
     */
    suspend fun getTrainingData(trainingId: Long): TrainingDataResult {
        return if (migrationFlags.useNewRepository) {
            try {
                val trainingSequence = newTrainingSequenceRepository.getTrainingSequence(trainingId)
                TrainingDataResult.NewSystem(trainingSequence)
            } catch (e: Exception) {
                // Fallback to legacy system if new system fails
                val legacyData = legacyFetchTrainingUseCase(trainingId)
                val convertedData = migrationAdapter.convertToTrainingSequence(legacyData)
                TrainingDataResult.LegacyFallback(convertedData, legacyData)
            }
        } else {
            val legacyData = legacyFetchTrainingUseCase(trainingId)
            if (migrationFlags.isDecoupledBreaksEnabled) {
                // Convert legacy data to new format for processing
                val convertedData = migrationAdapter.convertToTrainingSequence(legacyData)
                TrainingDataResult.HybridMode(convertedData, legacyData)
            } else {
                TrainingDataResult.LegacySystem(legacyData)
            }
        }
    }

    /**
     * Get break templates (only available in new system)
     */
    suspend fun getBreakTemplates() =
        if (migrationFlags.isDecoupledBreaksEnabled) {
            newTrainingSequenceRepository.getPopularBreaks()
        } else {
            emptyList()
        }

    /**
     * Create or get break template (only available in new system)
     */
    suspend fun getOrCreateBreakTemplate(duration: Int) =
        if (migrationFlags.isDecoupledBreaksEnabled) {
            newTrainingSequenceRepository.getOrCreateBreakTemplate(duration)
        } else {
            throw UnsupportedOperationException("Break templates not available in legacy system")
        }

    sealed class TrainingDataResult {
        data class NewSystem(val trainingSequence: TrainingSequence) : TrainingDataResult()
        data class LegacySystem(val trainingWithActivity: TrainingWithActivity) : TrainingDataResult()
        data class HybridMode(
            val trainingSequence: TrainingSequence,
            val legacyData: TrainingWithActivity
        ) : TrainingDataResult()
        data class LegacyFallback(
            val trainingSequence: TrainingSequence,
            val legacyData: TrainingWithActivity
        ) : TrainingDataResult()
    }
}
