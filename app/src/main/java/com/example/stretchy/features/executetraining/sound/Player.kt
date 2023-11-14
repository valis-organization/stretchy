package com.example.stretchy.features.executetraining.sound

import android.content.Context
import android.media.MediaPlayer
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.sound.data.SoundTrack

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

    fun stopSound() {
        for (player in players) {
            if (player.isPlaying) {
                player.stop()
                player.reset()
                player.release()
            }
        }
        players.clear()
    }

    private fun SoundTrack.toRawWav(): Int {
        return when (this) {
            SoundTrack.BREAK_ENDED -> R.raw.break_ended
            SoundTrack.EXERCISE_ENDING -> R.raw.exercise_ending
        }
    }
}

