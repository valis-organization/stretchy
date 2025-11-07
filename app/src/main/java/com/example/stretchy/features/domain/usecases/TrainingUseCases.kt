// ...existing code...
package com.example.stretchy.features.domain.usecases

import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity

class CreateTrainingUseCase(private val repository: Repository) {
    suspend operator fun invoke(training: TrainingWithActivity) {
        repository.addTrainingWithActivities(training)
    }
}

class EditTrainingUseCase(private val repository: Repository) {
    suspend operator fun invoke(trainingId: Long, editedTraining: TrainingWithActivity) {
        repository.editTrainingWithActivities(trainingId, editedTraining)
    }
}

class DeleteTrainingUseCase(private val repository: Repository) {
    suspend operator fun invoke(trainingId: Long) {
        repository.deleteTrainingById(trainingId)
    }
}

class FetchTrainingListUseCase(private val repository: Repository) {
    suspend operator fun invoke() = repository.getTrainingsWithActivities()
}

class FetchTrainingByIdUseCase(private val repository: Repository) {
    suspend operator fun invoke(id: Long) = repository.getTrainingWithActivitiesById(id)
}

class CopyTrainingUseCase(private val repository: Repository) {
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

// ...existing code...
