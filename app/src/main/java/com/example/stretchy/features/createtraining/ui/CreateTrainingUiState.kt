package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.repository.Activity

sealed class CreateTrainingUiState {
    data class Success(
        val currentName: String,
        val activities: List<Activity>
    ) : CreateTrainingUiState()

    data class Editing(
        val trainingId: Long,
        val trainingName: String,
        val activities: List<Activity>
    ) : CreateTrainingUiState()

    data class Error(val reason: Reason) : CreateTrainingUiState() {
        sealed class Reason {
            object MissingTrainingName : Reason()
            object NotEnoughExercises : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    object Init : CreateTrainingUiState()

    object Done : CreateTrainingUiState()
}

sealed class CreateTrainingActivityItem(
    open val duration: Float,
) {
    data class Exercise(
        val name: String,
        override val duration: Float,
    ) : CreateTrainingActivityItem(duration)

    data class Break(
        override val duration: Float,
    ) : CreateTrainingActivityItem(duration)
}