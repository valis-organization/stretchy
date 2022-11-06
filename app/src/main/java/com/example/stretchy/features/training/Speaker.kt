package com.example.stretchy.features.training

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class Speaker(
    context: Context,
    locale: Locale = Locale.ENGLISH,
    pitch: Float = 1f,
    speechSpeed: Float = 0.9f,
    onInitialized: () -> Unit
) {
    lateinit var textToSpeech: TextToSpeech
    private var initliazed: AtomicBoolean = AtomicBoolean(false)

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = textToSpeech.setLanguage(locale)
                if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported")
                } else {
                    initliazed.set(true)
                    textToSpeech.voice
                    textToSpeech.setPitch(pitch)
                    textToSpeech.setSpeechRate(speechSpeed);
                    onInitialized()
                }
            }
        }
    }

    @Throws(SpeakerUninitializedException::class)
    fun say(text: String) {
        if (initliazed.get()) {
            textToSpeech.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        } else {
            throw SpeakerUninitializedException()
        }
    }

    class SpeakerUninitializedException() : Exception()

    companion object {
        val TAG = "Speaker"
        val LOCALE_PL = Locale("pl_PL")

        //todo nice voice lines
        //en-us-x-sfg-network
        //en-us-x-iob-local
        //en-us-x-tpc-local
        //en-us-x-tpd-network
        //en-us-x-tpd-local
        //en-us-x-iom-local
        //en-us-x-iom-network
    }
}