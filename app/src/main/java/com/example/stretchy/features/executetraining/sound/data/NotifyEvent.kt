package com.example.stretchy.features.executetraining.sound.data

import com.example.stretchy.repository.Activity


sealed class NotifyEvent {
    object ActivityEnds : NotifyEvent()
    object ActivitySwiped : NotifyEvent()
    object TrainingEnds : NotifyEvent()
    data class ActivityUpdate(
        val currentActivity: Activity,
        val isFirstExercise: Boolean,
        val nextExerciseName: String?
    ) : NotifyEvent()
}