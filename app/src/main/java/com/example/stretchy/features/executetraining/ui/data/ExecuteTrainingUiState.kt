package com.example.stretchy.features.executetraining.ui.data

sealed class ExecuteTrainingUiState {
    object Loading : ExecuteTrainingUiState()
    object Error : ExecuteTrainingUiState()
    class Success(val activityItem: ActivityItem) : ExecuteTrainingUiState()
}

sealed class ActivityItem(
    open val nextExercise: String?,
    open val currentTime: Float,
    open val totalTime: Int,
) {
    data class Exercise(
        val name: String,
        override val nextExercise: String?,
        override val currentTime: Float,
        override val totalTime: Int
    ) : ActivityItem(nextExercise, currentTime, totalTime)

    data class Break(
        override val nextExercise: String,
        override val currentTime: Float,
        override val totalTime: Int
    ) : ActivityItem(nextExercise, currentTime, totalTime)
}