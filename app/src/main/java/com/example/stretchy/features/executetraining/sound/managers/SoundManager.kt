package com.example.stretchy.features.executetraining.sound.managers

import com.example.stretchy.features.executetraining.sound.data.NotifyEvent
import com.example.stretchy.features.executetraining.sound.data.SoundType
import kotlinx.coroutines.flow.Flow

interface SoundManager {
    val soundEventFlow: Flow<SoundType>

    suspend fun notifyEvent(event: NotifyEvent)
}
