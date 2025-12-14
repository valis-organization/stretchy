/**
 * Repository Adapters - Data Layer Operations
 *
 * IMPORTANT: These are NOT business use cases - they are simple repository adapters
 * Renamed classes from *UseCase to *RepoAdapter to follow Clean Architecture naming convention
 *
 * Business logic jest w klasach *DomainUseCase (TrainingDomainUseCases.kt)
 *
 * UWAGA: typealiasy zostały usunięte – ViewModel powinien wstrzykiwać bezpośrednio klasy
 * FetchTrainingListRepoAdapter, DeleteTrainingRepoAdapter, CopyTrainingRepoAdapter.
 */
package com.example.stretchy.features.domain.usecases

import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity

class CreateTrainingRepoAdapter(private val repository: Repository) {
    suspend operator fun invoke(training: TrainingWithActivity) {
        repository.addTrainingWithActivities(training)
    }
}

class EditTrainingRepoAdapter(private val repository: Repository) {
    suspend operator fun invoke(trainingId: Long, editedTraining: TrainingWithActivity) {
        repository.editTrainingWithActivities(trainingId, editedTraining)
    }
}

class DeleteTrainingRepoAdapter(private val repository: Repository) {
    suspend operator fun invoke(trainingId: Long) {
        repository.deleteTrainingById(trainingId)
    }
}

class FetchTrainingListRepoAdapter(private val repository: Repository) {
    suspend operator fun invoke() = repository.getTrainingsWithActivities()
}

class FetchTrainingByIdRepoAdapter(private val repository: Repository) {
    suspend operator fun invoke(id: Long) = repository.getTrainingWithActivitiesById(id)
}

class CopyTrainingRepoAdapter(private val repository: Repository) {
    suspend operator fun invoke(trainingId: Long, copySuffix: String = " copy") {
        val training = repository.getTrainingWithActivitiesById(trainingId)
        repository.addTrainingWithActivities(
            TrainingWithActivity(
                training.name + copySuffix,
                training.trainingType,
                true,
                training.activities
            )
        )
    }
}
