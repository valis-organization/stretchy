package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.repository.Activity

sealed class CreateTrainingUiState {
    data class Success(
        val training: List<Activity>
    ) : CreateTrainingUiState()

    data class Error(val reason: Reason,val exercises : List<Activity>) : CreateTrainingUiState() {
        sealed class Reason {
            object MissingTrainingName : Reason()
            object NotEnoughExercises : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    object Init : CreateTrainingUiState()

    object Done : CreateTrainingUiState()
}