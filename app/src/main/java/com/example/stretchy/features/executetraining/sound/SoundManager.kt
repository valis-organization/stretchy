package com.example.stretchy.features.executetraining.sound

import com.example.stretchy.features.executetraining.ui.data.event.ReadExerciseNameEvent
import com.example.stretchy.repository.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SoundManager {

    val soundState: StateFlow<SoundState>

    fun getReadExerciseNameFlow(): Flow<ReadExerciseNameEvent?>

    fun notifyEvent(event: NotifyEvent)
}

sealed class NotifyEvent {
    object ActivityEnds : NotifyEvent()
    object ActivitySwiped : NotifyEvent()
    data class ActivityUpdate(
        val currentActivity: Activity,
        val isFirstExercise: Boolean,
        val nextExerciseName: String?
    ) : NotifyEvent()
}