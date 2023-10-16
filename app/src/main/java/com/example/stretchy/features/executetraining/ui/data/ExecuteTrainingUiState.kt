package com.example.stretchy.features.executetraining.ui.data

import com.example.stretchy.features.executetraining.ui.data.event.ActivityFinishesEvent
import com.example.stretchy.features.executetraining.ui.data.event.BreakEndsEvent
import com.example.stretchy.features.executetraining.ui.data.event.ReadExerciseNameEvent
import com.example.stretchy.features.executetraining.ui.data.event.TrainingCompletedEvent


data class ExecuteTrainingUiState(
    var isLoading: Boolean,
    var error: Throwable?,
    var success: ActivityItem?,
    val trainingCompleted: TrainingCompleted?,
    //events
    val readExerciseNameEvent: ReadExerciseNameEvent?,
    val trainingCompletedEvent: TrainingCompletedEvent?,
    val activityFinishesEvent: ActivityFinishesEvent?,
    val breakEndsEvent: BreakEndsEvent?
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

    data class TimelessExercise(
        val name: String,
        override val nextExercise: String?,
        override val trainingProgressPercent: Float
    ) : ActivityItem(nextExercise, 0F, 0, trainingProgressPercent)

    data class Break(
        override val nextExercise: String,
        override val currentTime: Float,
        override val totalExerciseTime: Int,
        override val trainingProgressPercent: Float
    ) : ActivityItem(nextExercise, currentTime, totalExerciseTime, trainingProgressPercent)
}