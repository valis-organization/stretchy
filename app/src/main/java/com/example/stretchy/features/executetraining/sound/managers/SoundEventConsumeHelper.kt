package com.example.stretchy.features.executetraining.sound.managers

import android.content.Context
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.sound.SoundType
import com.example.stretchy.features.executetraining.sound.Speaker
import com.example.stretchy.features.executetraining.sound.data.SoundEvent
import com.example.stretchy.features.executetraining.sound.data.SoundTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun consumeSoundEvents(
    soundEvent: SoundEvent?,
    coroutineScope: CoroutineScope,
    speaker: Speaker,
    context: Context
) {
    when (soundEvent) {
        is SoundEvent.ActivityEndedEvent -> consumeActivityFinishes(
            soundEvent,
            coroutineScope,
            speaker
        )
        is SoundEvent.BreakEndedEvent -> consumeBreakEnds(
            soundEvent,
            coroutineScope,
            speaker
        )
        is SoundEvent.ReadExerciseNameEvent -> consumeReadExerciseName(
            soundEvent,
            coroutineScope,
            speaker
        )
        is SoundEvent.TrainingCompletedEvent -> consumeTrainingEnds(
            soundEvent,
            coroutineScope,
            speaker,
            context
        )
        is SoundEvent.SkipSounds -> {
            consumeSkipSounds(speaker)
        }
        null -> {}
    }
}

private fun consumeTrainingEnds(
    trainingCompletedEvent: SoundEvent.TrainingCompletedEvent,
    coroutineScope: CoroutineScope,
    speaker: Speaker,
    context: Context
) {
    trainingCompletedEvent.consume()?.let {
        coroutineScope.launch {
            speaker.queueSound(SoundType.Speech(context.resources.getString(R.string.training_finished)))
        }
    }
}

private fun consumeSkipSounds(speaker: Speaker) {
    speaker.stopSound()
}

private fun consumeReadExerciseName(
    readExerciseNameEvent: SoundEvent.ReadExerciseNameEvent,
    coroutineScope: CoroutineScope,
    speaker: Speaker
) {
    val name = readExerciseNameEvent.name
    if (!readExerciseNameEvent.isConsumed) {
        readExerciseNameEvent.consume().let {
            coroutineScope.launch {
                speaker.queueSound(SoundType.Speech(name))
            }
        }
    }
}

private fun consumeActivityFinishes(
    activityFinishesEvent: SoundEvent?,
    coroutineScope: CoroutineScope,
    speaker: Speaker
) {
    activityFinishesEvent?.consume()?.let {
        coroutineScope.launch {
            speaker.queueSound(SoundType.Sound(SoundTrack.EXERCISE_ENDING))
        }
    }
}

private fun consumeBreakEnds(
    breakEndsEvent: SoundEvent?,
    coroutineScope: CoroutineScope,
    speaker: Speaker
) {
    breakEndsEvent?.consume()?.let {
        coroutineScope.launch {
            speaker.queueSound(SoundType.Sound(SoundTrack.BREAK_ENDED))
        }
    }
}