package com.example.stretchy.features.executetraining

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class Speaker(
    context: Context,
    locale: Locale = Locale.ENGLISH,
    pitch: Float = 1f,
    speechSpeed: Float = 0.9f,
) {
    lateinit var textToSpeech: TextToSpeech
    private var isInitializedFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = textToSpeech.setLanguage(locale)
                if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported")
                } else {
                    textToSpeech.voice
                    textToSpeech.setPitch(pitch)
                    textToSpeech.setSpeechRate(speechSpeed)
                    GlobalScope.launch {
                        isInitializedFlow.emit(true)
                    }
                }
            }
        }
    }

    @Throws(SpeakerUninitializedException::class)
    suspend fun say(text: String) {
        isInitializedFlow
            .filter { it }
            .first()

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
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