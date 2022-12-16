package com.example.stretchy.features.executetraining.sound

import android.content.Context
import android.media.MediaPlayer
import com.example.stretchy.R

class Player(private val context: Context) {
    private val players: MutableList<MediaPlayer> = mutableListOf()

    fun playSound(soundTrack: SoundTrack) {
        val mp = MediaPlayer.create(context, soundTrack.toRawWav()).apply {
            setOnCompletionListener { mp ->
                mp.reset()
                mp.release()
                players.remove(mp)
            }
        }
        players.add(mp)
        mp.start()
    }

    private fun SoundTrack.toRawWav(): Int {
        return when (this) {
            SoundTrack.BREAK_ENDED -> R.raw.break_ended
            SoundTrack.EXERCISE_ENDING -> R.raw.exercise_ending
        }
    }
}

