package com.example.stretchy.features.executetraining.sound.managers

import com.example.stretchy.features.executetraining.sound.data.TrainingEvent
import com.example.stretchy.features.executetraining.sound.data.SoundType
import kotlinx.coroutines.flow.Flow

interface SoundEventNotifier {
    val soundEventFlow: Flow<SoundType>

    suspend fun notifyEvent(event: TrainingEvent)
}
