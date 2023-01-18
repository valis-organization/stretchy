package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.repository.Activity

sealed class CreateTrainingUiState(open val createTrainingButtonVisible: Boolean) {
    data class Success(
        val trainingId: Long?,
        val editingTraining: Boolean,
        val currentName: String,
        val activities: List<Activity>,
        override val createTrainingButtonVisible: Boolean
    ) : CreateTrainingUiState(createTrainingButtonVisible)

    data class Error(val reason: Reason) : CreateTrainingUiState(false) {
        sealed class Reason {
            object MissingTrainingName : Reason()
            object NotEnoughExercises : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    object Init : CreateTrainingUiState(false)

    object Done : CreateTrainingUiState(false)
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