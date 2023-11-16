package com.example.stretchy.features.executetraining.sound.data

import com.example.stretchy.repository.Activity


sealed class TrainingEvent {
    object ActivityEnds : TrainingEvent()
    object ActivitySwiped : TrainingEvent()
    object TrainingEnds : TrainingEvent()
    data class ActivityUpdate(
        val currentActivity: Activity,
        val isFirstExercise: Boolean,
        val nextExerciseName: String?
    ) : TrainingEvent()
}