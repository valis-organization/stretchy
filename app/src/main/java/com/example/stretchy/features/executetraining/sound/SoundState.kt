package com.example.stretchy.features.executetraining.sound

import com.example.stretchy.features.executetraining.ui.data.event.ActivityFinishesEvent
import com.example.stretchy.features.executetraining.ui.data.event.BreakEndsEvent
import com.example.stretchy.features.executetraining.ui.data.event.TrainingCompletedEvent

data class SoundState(
    val trainingCompletedEvent: TrainingCompletedEvent?,
    val activityFinishesEvent: ActivityFinishesEvent?,
    val breakEndsEvent: BreakEndsEvent?
)
