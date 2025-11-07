package com.example.stretchy.features.executetraining

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Improved Timer class that provides better lifecycle management and uses proper coroutine scope
 * instead of GlobalScope for better memory management and cancellation support.
 */
class ImprovedTimer(
    private val scope: CoroutineScope
) {
    private val _timerFlow = MutableStateFlow(0f)
    val flow: StateFlow<Float> = _timerFlow.asStateFlow()

    private var timerJob: Job? = null
    private var currentMs: Int = 0

    private var paused = true
    private var isRunning = false

    /**
     * Start the timer countdown
     */
    fun start() {
        if (isRunning && !paused) return

        paused = false
        isRunning = true

        timerJob?.cancel()
        timerJob = scope.launch {
            while (currentMs > 0 && !paused && isActive) {
                delay(10)
                if (!paused) {
                    currentMs -= 10
                    _timerFlow.emit(currentMs.toFloat())
                }
            }
            if (currentMs <= 0) {
                _timerFlow.emit(0f)
                isRunning = false
            }
        }
    }

    /**
     * Pause the timer
     */
    fun pause() {
        paused = true
        timerJob?.cancel()
    }



    /**
     * Set the duration of the timer in seconds
     */
    fun setDuration(seconds: Int) {
        val newDurationMs = seconds * 1000
        currentMs = newDurationMs

        _timerFlow.value = newDurationMs.toFloat()
    }



    /**
     * Clean up resources when the timer is no longer needed
     */
    fun cleanup() {
        timerJob?.cancel()
        isRunning = false
        paused = true
    }
}

/**
 * Factory function to create timer with proper scope
 */
fun createImprovedTimer(scope: CoroutineScope): ImprovedTimer {
    return ImprovedTimer(scope)
}
