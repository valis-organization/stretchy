package com.example.stretchy

import com.example.stretchy.ui.theme.ActivityItem

sealed class ExerciseUiState {
    object Loading : ExerciseUiState()
    object Error : ExerciseUiState()
    class Success(val data: ActivityItem) : ExerciseUiState()
}