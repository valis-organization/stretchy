package com.example.stretchy.features.traininglist.ui

import com.example.stretchy.theme.ExerciseListUiModel

sealed class ExercisePlansUiState {
    object Empty : ExercisePlansUiState()
    object Loading : ExercisePlansUiState()
    class Loaded(val data: ExerciseListUiModel) : ExercisePlansUiState()
}