package com.example.stretchy.features.traininglist.ui.data

sealed class TrainingListUiState {
    object Empty : TrainingListUiState()
    object Loading : TrainingListUiState()
    class Loaded(var trainings: List<Training>) : TrainingListUiState()
}

data class Training(
    val id: String,
    val itemName: String,
    val numberOfExercises: Int,
    val timeInSeconds: Int
)
