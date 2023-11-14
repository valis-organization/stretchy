package com.example.stretchy.features.executetraining.sound.managers

import android.content.Context
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.sound.Player
import com.example.stretchy.features.executetraining.sound.Speaker
import com.example.stretchy.features.executetraining.sound.data.SoundTrack
import com.example.stretchy.features.executetraining.sound.data.SoundType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SoundEventConsumer(
    private val coroutineScope: CoroutineScope,
    private val speaker: Speaker,
    private val player: Player,
    private val context: Context
) {

    fun consume(
        soundType: SoundType?,
    ) {
        when (soundType) {
            is SoundType.ActivityFinishesEvent -> consumeActivityFinishes(
                soundType,
                coroutineScope,
                player
            )
            is SoundType.BreakEndsEvent -> consumeBreakEnds(soundType, coroutineScope, player)
            is SoundType.ReadExerciseNameEvent -> consumeReadExerciseName(
                soundType,
                coroutineScope,
                speaker
            )
            is SoundType.TrainingCompletedEvent -> consumeTrainingEnds(
                soundType,
                coroutineScope,
                speaker,
                context
            )
            null -> {}
        }
    }

    private fun consumeTrainingEnds(
        trainingCompletedEvent: SoundType.TrainingCompletedEvent,
        coroutineScope: CoroutineScope,
        speaker: Speaker,
        context: Context
    ) {
        trainingCompletedEvent.consume()?.let {
            coroutineScope.launch {
                speaker.say(context.resources.getString(R.string.training_finished))
            }
        }
    }

    private fun consumeReadExerciseName(
        readExerciseNameEvent: SoundType.ReadExerciseNameEvent,
        coroutineScope: CoroutineScope,
        speaker: Speaker
    ) {
        val name = readExerciseNameEvent.name
        if (!readExerciseNameEvent.isConsumed) {
            readExerciseNameEvent.consume().let {

                coroutineScope.launch {
                    speaker.say(name)
                }
            }
        }
    }

    private fun consumeActivityFinishes(
        activityFinishesEvent: SoundType?,
        coroutineScope: CoroutineScope,
        player: Player
    ) {
        activityFinishesEvent?.consume()?.let {
            coroutineScope.launch {
                player.playSound(SoundTrack.EXERCISE_ENDING)
            }
        }
    }

    private fun consumeBreakEnds(
        breakEndsEvent: SoundType?,
        coroutineScope: CoroutineScope,
        player: Player
    ) {
        breakEndsEvent?.consume()?.let {
            coroutineScope.launch {
                player.playSound(SoundTrack.BREAK_ENDED)
            }
        }
    }

}