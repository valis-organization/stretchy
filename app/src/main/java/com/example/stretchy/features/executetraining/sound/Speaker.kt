package com.example.stretchy.features.executetraining.sound

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


class Speaker(
    val context: Context,
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
                    val voice = Voice(
                        "en-us-x-tpc-local",
                        Locale("en", "US"),
                        1000,
                        0,
                        true,
                        emptySet()
                    )
                    textToSpeech.voice = voice
                    textToSpeech.setPitch(pitch)
                    textToSpeech.setSpeechRate(speechSpeed)
                    GlobalScope.launch {
                        isInitializedFlow.emit(true)
                    }
                }
            }
        }
    }

    suspend fun say(text: String) {
        isInitializedFlow
            .filter { it }
            .onEach {
                delay(500)
            }
            .first()

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }

    companion object {
        val TAG = "Speaker"
        val LOCALE_PL = Locale("pl_PL")

        //todo nice voice lines
        //en-us-x-sfg-network
        //en-us-x-iob-local
        //en-us-x-tpc-local
        //en-us-x-tpd-network
        //en-us-x-iom-local
        //en-us-x-iom-network
    }

    //nice
    //en-us-x-iob-local - female
    //en-us-x-tpc-local - calm female
    //en-us-x-iom-local - calm male
}

