package com.example.stretchy.features.executetraining.sound

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.ui.data.event.ActivityFinishesEvent
import com.example.stretchy.features.executetraining.ui.data.event.BreakEndsEvent
import com.example.stretchy.features.executetraining.ui.data.event.ReadExerciseNameEvent
import com.example.stretchy.repository.Activity
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce

class SoundManagerImpl : SoundManager {
    private var currentActivity: Activity? = null
    private val readExerciseNameEvent: MutableStateFlow<ReadExerciseNameEvent?> =
        MutableStateFlow(null)
    private val _soundState = MutableStateFlow(
        SoundState(
            null,
            null,
            null
        )
    )
    override val soundState: StateFlow<SoundState> = _soundState

    @OptIn(FlowPreview::class)
    override fun getDebouncedReadExerciseNameFlow(): Flow<ReadExerciseNameEvent?> {
        return readExerciseNameEvent.debounce(600)
    }

    private var lastActivityBreakSoundPosted: Long? = null
    private var activityFinishedEventPosted: Long? = null
    private var lastActivityNamePosted: String? = null

    override suspend fun notifyActivityUpdated(
        currentActivity: Activity,
        isFirstExercise: Boolean,
        nextExerciseName: String?
    ) {
        if (this.currentActivity != currentActivity) {
            if (isFirstExercise) {
                readExerciseNameEvent.emit(ReadExerciseNameEvent(currentActivity.name))
            } else if (currentActivity.activityType == ActivityType.BREAK && nextExerciseName != null && lastActivityNamePosted != nextExerciseName) {
                lastActivityNamePosted = nextExerciseName
                readExerciseNameEvent.emit(ReadExerciseNameEvent(nextExerciseName))
            }
            this.currentActivity = currentActivity
        }
    }

    override fun notifyActivityEnds() {
        val activityType = currentActivity?.activityType
        val currentActivityId = currentActivity?.activityId
        if (activityType == ActivityType.BREAK) {
            if (lastActivityBreakSoundPosted != currentActivityId) {
                lastActivityBreakSoundPosted = currentActivityId
                _soundState.value = _soundState.value.copy(breakEndsEvent = BreakEndsEvent())
            }
        } else if (activityType == ActivityType.EXERCISE || activityType == ActivityType.STRETCH) {
            if (activityFinishedEventPosted != currentActivityId) {
                activityFinishedEventPosted = currentActivityId
                _soundState.value =
                    _soundState.value.copy(activityFinishesEvent = ActivityFinishesEvent())
            }
        }
    }

    override suspend fun notifyActivitySwiped() {
        if (lastActivityNamePosted != currentActivity?.name) {
            lastActivityNamePosted = currentActivity?.name
            readExerciseNameEvent.emit(ReadExerciseNameEvent(currentActivity?.name))
        }
    }

}