package com.example.stretchy.features.executetraining.ui.data.event

import com.example.stretchy.common.OneTimeEvent

class TrainingCompletedEvent(override var value: Unit? = Unit) : OneTimeEvent<Unit>()