package com.example.stretchy.features.training.ui.data

sealed class ExerciseUiState {
    object Loading : ExerciseUiState()
    object Error : ExerciseUiState()
    class Success(val data: ExercisesUiModel) : ExerciseUiState()
}