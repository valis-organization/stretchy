package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.features.training.ui.data.ActivityItem

sealed class TrainingUiState {
    data class Success(
        val training: List<ActivityItem>
    ) : TrainingUiState()

    data class Error(val reason: Reason) : TrainingUiState() {
        sealed class Reason {
            object MissingTrainingName : Reason()
            object NotEnoughExercises : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    object Init : TrainingUiState()

    object Done : TrainingUiState()
}