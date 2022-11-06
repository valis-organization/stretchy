package com.example.stretchy

import com.example.stretchy.ui.theme.ExercisesUiModel

sealed class ExerciseUiState {
    object Loading : ExerciseUiState()
    object Error : ExerciseUiState()
    class Success(val data: ExercisesUiModel) : ExerciseUiState()
}