package com.example.stretchy.features.executetraining.ui.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.features.executetraining.ImprovedTimer
import com.example.stretchy.features.executetraining.ui.composable.timer.AnalogTimerClock
import com.example.stretchy.features.executetraining.ui.composable.timer.MinimalAnalogTimer
import com.example.stretchy.features.executetraining.ui.composable.timer.TimerVieww
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Timer Manager that handles the lifecycle and state of timers
 */
@HiltViewModel
class TimerManager @Inject constructor() : ViewModel() {

    private var currentTimer: ImprovedTimer? = null

    val isRunning: StateFlow<Boolean>?
        get() = currentTimer?.isRunning

    val timeRemaining: StateFlow<Long>?
        get() = currentTimer?.timeRemaining

    val progress: StateFlow<Float>?
        get() = currentTimer?.progress

    val timeRemainingInSeconds: StateFlow<Int>?
        get() = currentTimer?.timeRemainingInSeconds

    fun createTimer(): ImprovedTimer {
        // Clean up existing timer if any
        currentTimer?.cleanup()

        // Create new timer with ViewModel scope for proper lifecycle management
        currentTimer = ImprovedTimer(viewModelScope)
        return currentTimer!!
    }

    fun getCurrentTimer(): ImprovedTimer? = currentTimer

    override fun onCleared() {
        super.onCleared()
        currentTimer?.cleanup()
    }
}

/**
 * Timer Display Type Enum for easy switching between visualizations
 */
enum class TimerDisplayType {
    ARC,           // Current implementation - arc progress
    ANALOG_FULL,   // Full analog clock with hands and markers
    ANALOG_MINIMAL, // Minimal analog with just progress arc
}

/**
 * Unified Timer Composable that can switch between different display types
 */
@Composable
fun UniversalTimer(
    currentSeconds: Float,
    totalSeconds: Float,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    displayType: TimerDisplayType = TimerDisplayType.ARC,
    isBreak: Boolean = false
) {
    when (displayType) {
        TimerDisplayType.ARC -> {
            TimerVieww(
                modifier = modifier,
                isBreak = isBreak,
                totalSeconds = totalSeconds,
                currentSeconds = currentSeconds
            )
        }

        TimerDisplayType.ANALOG_FULL -> {
            AnalogTimerClock(
                modifier = modifier,
                currentSeconds = currentSeconds,
                totalSeconds = totalSeconds,
                isBreak = isBreak,
                showDigitalTime = true
            )
        }

        TimerDisplayType.ANALOG_MINIMAL -> {
            MinimalAnalogTimer(
                modifier = modifier,
                currentSeconds = currentSeconds,
                totalSeconds = totalSeconds,
                isBreak = isBreak
            )
        }
    }
}

/**
 * Hook for managing timer lifecycle in Composables
 */
@Composable
fun rememberTimer(timerManager: TimerManager): ImprovedTimer {
    val timer = timerManager.getCurrentTimer() ?: timerManager.createTimer()

    // Cleanup when composable leaves composition
    DisposableEffect(timer) {
        onDispose {
            // Don't cleanup here - let ViewModel handle it
            // timer.cleanup()
        }
    }

    return timer
}

/**
 * Composable that provides timer state as Compose State
 */
@Composable
fun TimerState(
    timer: ImprovedTimer,
    content: @Composable (
        timeRemaining: Long,
        isRunning: Boolean,
        progress: Float,
        timeInSeconds: Int
    ) -> Unit
) {
    val timeRemaining by timer.timeRemaining.collectAsState()
    val isRunning by timer.isRunning.collectAsState()
    val progress by timer.progress.collectAsState()
    val timeInSeconds by timer.timeRemainingInSeconds.collectAsState()

    content(timeRemaining, isRunning, progress, timeInSeconds)
}

/**
 * Extension functions for easy timer operations
 */
fun ImprovedTimer.togglePlayPause() {
    if (isRunning.value) {
        pause()
    } else {
        start()
    }
}

fun ImprovedTimer.restart() {
    stop()
    start()
}

/**
 * Timer Configuration Class for easy setup
 */
data class TimerConfig(
    val displayType: TimerDisplayType = TimerDisplayType.ARC,
    val showDigitalOverlay: Boolean = true,
    val autoStart: Boolean = true,
    val soundEnabled: Boolean = true
)

/**
 * High-level timer component that combines timer logic with UI
 */
@Composable
fun ConfigurableTimer(
    durationInSeconds: Int,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    config: TimerConfig = TimerConfig(),
    isBreak: Boolean = false,
    onTimerFinished: () -> Unit = {},
    onTimerTick: (remainingSeconds: Int) -> Unit = {}
) {
    // This would need to be integrated with your existing ViewModel
    // For now, this shows the structure for a self-contained timer component

    UniversalTimer(
        currentSeconds = (durationInSeconds * 1000).toFloat(), // Placeholder
        totalSeconds = (durationInSeconds * 1000).toFloat(),
        displayType = config.displayType,
        isBreak = isBreak,
        modifier = modifier
    )
}
