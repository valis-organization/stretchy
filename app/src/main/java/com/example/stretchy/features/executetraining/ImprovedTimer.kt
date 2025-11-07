package com.example.stretchy.features.executetraining

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Improved Timer implementation that fixes memory leaks and performance issues
 * from the original Timer class.
 */
class ImprovedTimer(private val scope: CoroutineScope) {

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var timerJob: Job? = null
    private var totalDuration: Long = 0L

    /**
     * Progress as a percentage (0.0 to 1.0)
     */
    val progress: StateFlow<Float> = _timeRemaining.map { remaining ->
        if (totalDuration <= 0) 0f
        else (totalDuration - remaining).toFloat() / totalDuration.toFloat()
    }.stateIn(scope, SharingStarted.WhileSubscribed(), 0f)

    /**
     * Time remaining in seconds (for display purposes)
     */
    val timeRemainingInSeconds: StateFlow<Int> = _timeRemaining.map { millis ->
        (millis / 1000).toInt()
    }.stateIn(scope, SharingStarted.WhileSubscribed(), 0)

    fun start() {
        if (_isRunning.value) return

        _isRunning.value = true

        timerJob = scope.launch {
            while (_timeRemaining.value > 0 && _isRunning.value) {
                delay(100) // Update every 100ms instead of 10ms
                _timeRemaining.value = maxOf(0, _timeRemaining.value - 100)
            }

            // Timer finished
            if (_timeRemaining.value <= 0) {
                _isRunning.value = false
            }
        }
    }

    fun pause() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun stop() {
        _isRunning.value = false
        timerJob?.cancel()
        _timeRemaining.value = 0L
        totalDuration = 0L
    }

    fun setDuration(seconds: Int) {
        val millis = seconds * 1000L
        totalDuration = millis
        _timeRemaining.value = millis
    }

    fun addTime(seconds: Int) {
        val additionalMillis = seconds * 1000L
        totalDuration += additionalMillis
        _timeRemaining.value += additionalMillis
    }

    fun isFinished(): Boolean = _timeRemaining.value <= 0L

    /**
     * Clean up resources when the timer is no longer needed
     */
    fun cleanup() {
        timerJob?.cancel()
        _isRunning.value = false
    }
}

/**
 * Factory function to create timer with proper scope
 */
fun createTimer(scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())): ImprovedTimer {
    return ImprovedTimer(scope)
}
