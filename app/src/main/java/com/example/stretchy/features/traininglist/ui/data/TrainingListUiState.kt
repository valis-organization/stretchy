package com.example.stretchy.features.traininglist.ui.data

sealed class TrainingListUiState {
    object Empty : TrainingListUiState()
    object Loading : TrainingListUiState()
    data class Loaded(val trainings: List<Training>) : TrainingListUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : TrainingListUiState()
}

data class Training(
    val id: String,
    val name: String,
    val numberOfExercises: Int,
    val timeInSeconds: Int,
    val type: Type
) {
    enum class Type {
        STRETCH,
        BODY_WEIGHT
    }
}


