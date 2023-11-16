package com.example.stretchy.features.executetraining.sound.managers

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.sound.data.TrainingEvent
import com.example.stretchy.features.executetraining.sound.data.SoundType
import com.example.stretchy.repository.Activity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SoundEventNotifierImpl : SoundEventNotifier {
    private val soundsEventFlow: MutableSharedFlow<SoundType> = MutableSharedFlow(replay = 0)
    private val readExerciseNameEvent: MutableSharedFlow<SoundType.ReadExerciseNameEvent> =
        MutableSharedFlow(replay = 0)
    override val soundEventFlow: Flow<SoundType> = merge(soundsEventFlow, readExerciseNameFlow())

    private var currentActivity: Activity? = null
    private var lastActivityNamePosted: String? = null

    @OptIn(FlowPreview::class)
    fun readExerciseNameFlow(): Flow<SoundType> = readExerciseNameEvent
        .debounce(600)

    override suspend fun notifyEvent(event: TrainingEvent) {
        when (event) {
            TrainingEvent.ActivityEnds -> onActivityEnd()
            TrainingEvent.ActivitySwiped -> onActivitySwiped()
            is TrainingEvent.ActivityUpdate -> onActivityUpdate(
                event.currentActivity,
                event.isFirstExercise,
                event.nextExerciseName
            )
            TrainingEvent.TrainingEnds -> onTrainingEnd()
        }
    }

    private suspend fun onActivityUpdate(
        updatedActivity: Activity,
        isFirstExercise: Boolean,
        nextExerciseName: String?
    ) {
        if (this.currentActivity != updatedActivity) {
            currentActivity = updatedActivity
            if (isFirstExercise) {
                readExerciseNameEvent.emit(SoundType.ReadExerciseNameEvent(updatedActivity.name))
            } else if (shouldPostReadExerciseNameEvent(nextExerciseName)) {
                lastActivityNamePosted = nextExerciseName
                readExerciseNameEvent.emit(SoundType.ReadExerciseNameEvent(nextExerciseName!!))
            }
        }
    }

    private suspend fun onActivityEnd() {
        val activityType = currentActivity?.activityType
        if (activityType == ActivityType.BREAK) {
            soundsEventFlow.emit(SoundType.BreakEndedEvent())
        } else if (activityType == ActivityType.EXERCISE || activityType == ActivityType.STRETCH) {
            soundsEventFlow.emit(SoundType.ActivityEndedEvent())
        }
    }

    private suspend fun onActivitySwiped() {
        if (lastActivityNamePosted != currentActivity?.name) {
            lastActivityNamePosted = currentActivity?.name
            readExerciseNameEvent.emit(
                SoundType.ReadExerciseNameEvent(currentActivity?.name!!)
            )
        }
    }

    private suspend fun onTrainingEnd() {
        soundsEventFlow.emit(SoundType.TrainingCompletedEvent())
    }

    private fun shouldPostReadExerciseNameEvent(nextExerciseName: String?): Boolean =
        currentActivity?.activityType == ActivityType.BREAK && nextExerciseName != null && lastActivityNamePosted != nextExerciseName
}