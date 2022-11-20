package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.features.executetraining.ui.data.ActivityItem

sealed class CreateTrainingUiState {
    data class Success(
        val training: List<ActivityItem>
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