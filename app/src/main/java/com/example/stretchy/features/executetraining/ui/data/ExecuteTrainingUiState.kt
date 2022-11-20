package com.example.stretchy.features.executetraining.ui.data

sealed class ExecuteTrainingUiState {
    object Loading : ExecuteTrainingUiState()
    object Error : ExecuteTrainingUiState()
    class TrainingCompleted(val timeSpent : String) : ExecuteTrainingUiState()
    class Success(val activityItem: ActivityItem) : ExecuteTrainingUiState()
}

sealed class ActivityItem(
    open val nextExercise: String?,
    open val currentTime: Float,
    open val totalExerciseTime: Int,
    open val trainingProgressPercent: Int
) {
    data class Exercise(
        val exerciseName: String,
        override val nextExercise: String?,
        override val currentTime: Float,
        override val totalExerciseTime: Int,
        override val trainingProgressPercent: Int
    ) : ActivityItem(nextExercise, currentTime, totalExerciseTime,trainingProgressPercent)

    data class Break(
        override val nextExercise: String,
        override val currentTime: Float,
        override val totalExerciseTime: Int,
        override val trainingProgressPercent: Int
    ) : ActivityItem(nextExercise, currentTime, totalExerciseTime,trainingProgressPercent)
}