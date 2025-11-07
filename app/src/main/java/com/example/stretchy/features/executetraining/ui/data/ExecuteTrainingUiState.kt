package com.example.stretchy.features.executetraining.ui.data

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.sound.data.SoundEvent

data class ExecuteTrainingUiState(
    val isLoading: Boolean,
    val error: Throwable?,
    val displayableActivityItemListWithBreakMerged: List<ActivityItemExerciseAndBreakMerged>?,
    val trainingCompleted: TrainingCompleted?,
    val currentSeconds: Float,
    val trainingProgressPercent: Float,
    val activityTypes: List<ActivityType>?,
    val currentDisplayPage: Int,
    //events
    val soundEvent: SoundEvent?
)

class TrainingCompleted(val currentTrainingTime: String, val numberOfExercises: Int)

data class ActivityItemExerciseAndBreakMerged(
    val exercise: DisplayableActivityItem,
    val breakItem: DisplayableActivityItem.Break?
)

sealed class DisplayableActivityItem(
    open val nextExercise: String?,
    open val currentTime: Float,
    open val totalExerciseTime: Int
) {
    data class Exercise(
        val name: String,
        override val nextExercise: String?,
        override val currentTime: Float,
        override val totalExerciseTime: Int
    ) : DisplayableActivityItem(
        nextExercise,
        currentTime,
        totalExerciseTime,
    )

    data class TimelessExercise(
        val name: String,
        override val nextExercise: String?,
    ) : DisplayableActivityItem(nextExercise, 0F, 0)

    data class Break(
        override val nextExercise: String,
        override val currentTime: Float,
        override val totalExerciseTime: Int
    ) : DisplayableActivityItem(
        nextExercise,
        currentTime,
        totalExerciseTime
    )

    fun toActivityType(): ActivityType {
        return when (this) {
            is Break -> ActivityType.BREAK
            is Exercise -> ActivityType.EXERCISE
            is TimelessExercise -> ActivityType.TIMELESS_EXERCISE
        }
    }
}
