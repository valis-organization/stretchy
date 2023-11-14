package com.example.stretchy.features.executetraining.sound.managers

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.sound.data.NotifyEvent
import com.example.stretchy.features.executetraining.sound.data.SoundType
import com.example.stretchy.repository.Activity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SoundManagerImpl : SoundManager {
    private val soundsEventFlow: MutableSharedFlow<SoundType> = MutableSharedFlow(replay = 0)
    private val readExerciseNameEvent: MutableSharedFlow<SoundType.ReadExerciseNameEvent> =
        MutableSharedFlow(replay = 0)

    override val soundFlow: Flow<SoundType> = merge(soundsEventFlow, readExerciseNameFlow())

    private var currentActivity: Activity? = null
    private var lastActivityNamePosted: String? = null

    @OptIn(FlowPreview::class)
    fun readExerciseNameFlow(): Flow<SoundType> = readExerciseNameEvent
        .debounce(600)

    override suspend fun notifyEvent(event: NotifyEvent) {
        when (event) {
            NotifyEvent.ActivityEnds -> activityEnds()
            NotifyEvent.ActivitySwiped -> activitySwiped()
            is NotifyEvent.ActivityUpdate -> activityUpdate(
                event.currentActivity,
                event.isFirstExercise,
                event.nextExerciseName
            )
            NotifyEvent.TrainingEnds -> trainingEnds()
        }
    }

    private suspend fun activityUpdate(
        currentActivity: Activity,
        isFirstExercise: Boolean,
        nextExerciseName: String?
    ) {
        if (this.currentActivity != currentActivity) {
            if (isFirstExercise) {
                readExerciseNameEvent.emit(SoundType.ReadExerciseNameEvent(currentActivity.name))
            } else if (currentActivity.activityType == ActivityType.BREAK && nextExerciseName != null && lastActivityNamePosted != nextExerciseName) {
                lastActivityNamePosted = nextExerciseName
                readExerciseNameEvent.emit(SoundType.ReadExerciseNameEvent(nextExerciseName))
            }
            this.currentActivity = currentActivity
        }
    }

    private suspend fun activityEnds() {
        val activityType = currentActivity?.activityType
        if (activityType == ActivityType.BREAK) {
            soundsEventFlow.emit(SoundType.BreakEndsEvent())
        } else if (activityType == ActivityType.EXERCISE || activityType == ActivityType.STRETCH) {
            soundsEventFlow.emit(SoundType.ActivityFinishesEvent())
        }
    }

    private suspend fun activitySwiped() {
        if (lastActivityNamePosted != currentActivity?.name) {
            lastActivityNamePosted = currentActivity?.name
            readExerciseNameEvent.emit(
                SoundType.ReadExerciseNameEvent(currentActivity?.name!!)
            )
        }
    }

    private suspend fun trainingEnds() {
        soundsEventFlow.emit(SoundType.TrainingCompletedEvent())
    }

}