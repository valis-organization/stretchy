package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.repository.Activity

sealed class CreateTrainingUiState(open val createTrainingButtonVisible: Boolean) {
    data class Success(
        val training: List<Activity>,
        override val createTrainingButtonVisible: Boolean
    ) : CreateTrainingUiState(createTrainingButtonVisible)

    data class Error(val reason: Reason) : CreateTrainingUiState(false) {
        sealed class Reason {
            object MissingTrainingName : Reason()
            object NotEnoughExercises : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    data class TitleChanged(
        val training: List<Activity>,
        override val createTrainingButtonVisible: Boolean
    ) : CreateTrainingUiState(createTrainingButtonVisible)

    object Init : CreateTrainingUiState(false)

    object Done : CreateTrainingUiState(false)
}