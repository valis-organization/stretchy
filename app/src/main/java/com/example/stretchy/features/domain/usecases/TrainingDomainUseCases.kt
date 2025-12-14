package com.example.stretchy.features.domain.usecases

import com.example.stretchy.features.createtraining.domain.TrainingDomain
import com.example.stretchy.features.createtraining.domain.toDomainModel
import com.example.stretchy.features.createtraining.domain.toRepositoryModel
import com.example.stretchy.repository.Repository

/**
 * Domain-based Training Use Cases - Business Logic Layer
 *
 * Clean layer that works with domain models and provides validation
 *
 * TODO: Rename these classes to remove "Domain" suffix:
 * - CreateTrainingDomainUseCase → CreateTrainingUseCase
 * - EditTrainingDomainUseCase → EditTrainingUseCase
 * - FetchTrainingDomainUseCase → FetchTrainingUseCase
 *
 * These contain the actual business logic, unlike repository adapters
 */

/**
 * Domain-based Create Training Use Case
 * Works with clean domain models instead of repository models directly
 */
class CreateTrainingDomainUseCase(private val repository: Repository) {
    suspend operator fun invoke(training: TrainingDomain) {
        require(training.isValid()) { "Training is not valid: ${training.name}" }

        val repositoryModel = training.toRepositoryModel()
        repository.addTrainingWithActivities(repositoryModel)
    }
}

/**
 * Domain-based Edit Training Use Case
 * Handles training updates through domain layer with validation
 */
class EditTrainingDomainUseCase(private val repository: Repository) {
    suspend operator fun invoke(trainingId: Long, editedTraining: TrainingDomain) {
        require(trainingId > 0) { "Invalid training ID: $trainingId" }
        require(editedTraining.isValid()) { "Edited training is not valid: ${editedTraining.name}" }

        val repositoryModel = editedTraining.toRepositoryModel()
        repository.editTrainingWithActivities(trainingId, repositoryModel)
    }
}

/**
 * Domain-based Fetch Training Use Case
 * Returns clean domain models instead of repository models
 */
class FetchTrainingDomainUseCase(private val repository: Repository) {
    suspend operator fun invoke(id: Long): TrainingDomain {
        require(id > 0) { "Invalid training ID: $id" }

        val repositoryModel = repository.getTrainingWithActivitiesById(id)
        return repositoryModel.toDomainModel()
    }
}

/**
 * Domain-based Fetch Training List Use Case
 * Returns list of clean domain models
 */
class FetchTrainingListDomainUseCase(private val repository: Repository) {
    suspend operator fun invoke(): List<TrainingDomain> {
        val repositoryModels = repository.getTrainingsWithActivities()
        return repositoryModels.map { it.toDomainModel() }
    }
}

/**
 * Domain-based Copy Training Use Case
 * Works with domain validation and clean models
 */
class CopyTrainingDomainUseCase(private val repository: Repository) {
    suspend operator fun invoke(trainingId: Long, copySuffix: String = " copy") {
        require(trainingId > 0) { "Invalid training ID: $trainingId" }

        val originalTraining = FetchTrainingDomainUseCase(repository)(trainingId)
        val copiedTraining = originalTraining.copy(
            id = null, // Clear ID for new training
            name = originalTraining.name + copySuffix
        )

        CreateTrainingDomainUseCase(repository)(copiedTraining)
    }
}
