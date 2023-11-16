package com.example.stretchy.features.executetraining.sound.data

import com.example.stretchy.common.OneTimeEvent

sealed class SoundType(override var value: Unit? = Unit) : OneTimeEvent<Unit>() {
    class TrainingCompletedEvent : SoundType()

    class ActivityEndedEvent : SoundType()

    class BreakEndedEvent : SoundType()

    data class ReadExerciseNameEvent(var name: String) : SoundType()
}
