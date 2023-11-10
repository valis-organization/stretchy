package com.example.stretchy.features.executetraining.sound

import com.example.stretchy.features.executetraining.ui.data.event.ReadExerciseNameEvent
import com.example.stretchy.repository.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SoundManager {

    val soundState: StateFlow<SoundState>

    fun getDebouncedReadExerciseNameFlow(): Flow<ReadExerciseNameEvent?>

    fun notifyActivityEnds()

    suspend fun notifyActivitySwiped()

    suspend fun notifyActivityUpdated(
        currentActivity: Activity,
        isFirstExercise: Boolean,
        nextExerciseName: String?
    )
}