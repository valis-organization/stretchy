package com.example.stretchy.features.executetraining.sound

import com.example.stretchy.repository.Activity
import kotlinx.coroutines.flow.Flow

interface SoundManager {
    val soundFlow: Flow<SoundType>

    suspend fun notifyEvent(event: NotifyEvent)
}

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