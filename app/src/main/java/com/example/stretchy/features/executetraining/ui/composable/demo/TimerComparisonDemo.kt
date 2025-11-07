package com.example.stretchy.features.executetraining.ui.composable.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stretchy.features.executetraining.ui.timer.TimerDisplayType
import com.example.stretchy.features.executetraining.ui.timer.UniversalTimer

/**
 * Demo composable showing how to easily integrate the new timer system
 * This shows all timer display options side by side for comparison
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerComparisonDemo(
    modifier: Modifier = Modifier
) {
    var currentSeconds by remember { mutableFloatStateOf(45000f) } // 45 seconds
    val totalSeconds = 60000f // 60 seconds
    var isBreak by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(TimerDisplayType.ARC) }

    // Demo timer countdown (not for production)
    LaunchedEffect(currentSeconds) {
        if (currentSeconds > 0) {
            kotlinx.coroutines.delay(100)
            currentSeconds = maxOf(0f, currentSeconds - 100f)
        } else {
            // Reset timer for continuous demo
            currentSeconds = totalSeconds
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Timer Visualization Options",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Controls
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isBreak = !isBreak }
            ) {
                Text(if (isBreak) "Exercise Mode" else "Break Mode")
            }

            Button(
                onClick = { currentSeconds = totalSeconds }
            ) {
                Text("Reset")
            }
        }

        // Display type selector
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimerDisplayType.values().forEach { type ->
                FilterChip(
                    onClick = { selectedType = type },
                    label = {
                        Text(when(type) {
                            TimerDisplayType.ARC -> "Current"
                            TimerDisplayType.ANALOG_FULL -> "Analog Full"
                            TimerDisplayType.ANALOG_MINIMAL -> "Analog Mini"
                        })
                    },
                    selected = selectedType == type
                )
            }
        }

        // Selected timer display
        Card(
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selected: ${selectedType.name}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                UniversalTimer(
                    currentSeconds = currentSeconds,
                    totalSeconds = totalSeconds,
                    modifier = Modifier.size(250.dp),
                    displayType = selectedType,
                    isBreak = isBreak
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Time: ${(currentSeconds / 1000).toInt()}s / ${(totalSeconds / 1000).toInt()}s"
                )
            }
        }

        // Comparison grid showing all types
        Text(
            text = "All Timer Types Comparison",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TimerDisplayType.values().forEach { displayType ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when(displayType) {
                            TimerDisplayType.ARC -> "Current Arc Timer"
                            TimerDisplayType.ANALOG_FULL -> "Full Analog Clock"
                            TimerDisplayType.ANALOG_MINIMAL -> "Minimal Analog Timer"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    UniversalTimer(
                        currentSeconds = currentSeconds,
                        totalSeconds = totalSeconds,
                        modifier = Modifier.size(180.dp),
                        displayType = displayType,
                        isBreak = isBreak
                    )
                }
            }
        }
    }
}

/**
 * Easy integration example for your existing ExerciseComposable
 * Replace the current TimerVieww with this UniversalTimer
 */
@Composable
fun ExerciseTimerReplacement(
    currentTime: Float,
    totalTime: Float,
    isBreak: Boolean = false,
    timerType: TimerDisplayType = TimerDisplayType.ARC
) {
    // This is a drop-in replacement for your current TimerVieww
    UniversalTimer(
        currentSeconds = currentTime,
        totalSeconds = totalTime * 1000f, // Convert seconds to milliseconds if needed
        modifier = Modifier.size(300.dp),
        displayType = timerType,
        isBreak = isBreak
    )
}

@Preview(showBackground = true)
@Composable
fun TimerComparisonDemoPreview() {
    MaterialTheme {
        TimerComparisonDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseTimerReplacementPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Current Timer Style:")
            ExerciseTimerReplacement(
                currentTime = 25000f,
                totalTime = 60f,
                timerType = TimerDisplayType.ARC
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("New Analog Clock Style:")
            ExerciseTimerReplacement(
                currentTime = 25000f,
                totalTime = 60f,
                timerType = TimerDisplayType.ANALOG_FULL
            )
        }
    }
}
