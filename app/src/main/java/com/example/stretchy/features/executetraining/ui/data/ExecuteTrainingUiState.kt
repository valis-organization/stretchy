package com.example.stretchy.features.executetraining.ui.data


data class ExecuteTrainingUiState(
    var isLoading: Boolean,
    var error: Throwable?,
    var success: ActivityItem?,
    val readExerciseNameEvent: ReadExerciseNameEvent?,
    val trainingCompleted: TrainingCompleted?,
    val trainingCompletedEvent: TrainingCompletedEvent?
)

class TrainingCompleted(val currentTrainingTime: String, val numberOfExercises: Int)

sealed class ActivityItem(
    open val nextExercise: String?,
    open val currentTime: Float,
    open val totalExerciseTime: Int,
    open val trainingProgressPercent: Float
) {
    data class Exercise(
        val name: String,
        override val nextExercise: String?,
        override val currentTime: Float,
        override val totalExerciseTime: Int,
        override val trainingProgressPercent: Float
    ) : ActivityItem(nextExercise, currentTime, totalExerciseTime, trainingProgressPercent)

    data class Break(
        override val nextExercise: String,
        override val currentTime: Float,
        override val totalExerciseTime: Int,
        override val trainingProgressPercent: Float
    ) : ActivityItem(nextExercise, currentTime, totalExerciseTime, trainingProgressPercent)
}