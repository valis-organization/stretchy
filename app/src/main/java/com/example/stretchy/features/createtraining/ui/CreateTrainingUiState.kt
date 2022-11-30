package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.repository.Activity

sealed class CreateTrainingUiState(open val createTrainingButtonVisible: Boolean) {
    data class Success(
        override val createTrainingButtonVisible: Boolean,
        val training: List<Activity>
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