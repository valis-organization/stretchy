package com.example.stretchy.features.executetraining.ui.composable.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.theme.AzureBlue
import java.util.*
import kotlin.math.*

/**
 * Analog Clock Timer Composable
 * Displays remaining time as an analog clock with customizable appearance
 */
@Composable
fun AnalogTimerClock(
    currentSeconds: Float,
    totalSeconds: Float,
    modifier: Modifier = Modifier,
    isBreak: Boolean = false,
    showDigitalTime: Boolean = true,
    clockSize: Dp = 200.dp,
    strokeWidth: Dp = 8.dp
) {
    val progress = if (totalSeconds > 0) currentSeconds / totalSeconds else 0f

    // Animated progress for smooth transitions
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "timer_progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(clockSize)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawAnalogClock(
                progress = animatedProgress,
                isBreak = isBreak,
                strokeWidth = strokeWidth.toPx()
            )
        }

        if (showDigitalTime) {
            DigitalTimeDisplay(
                currentSeconds = currentSeconds,
                isBreak = isBreak
            )
        }
    }
}

/**
 * Draws the analog clock face with progress indication
 */
private fun DrawScope.drawAnalogClock(
    progress: Float,
    isBreak: Boolean,
    strokeWidth: Float
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = minOf(size.width, size.height) / 2f - strokeWidth

    // Colors based on timer type
    val primaryColor = if (isBreak) Color.White else AzureBlue
    val backgroundColor = primaryColor.copy(alpha = 0.2f)
    val progressColor = primaryColor

    // Draw clock face background
    drawCircle(
        color = Color.White,
        radius = radius + strokeWidth / 2,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )

    // Draw hour markers
    drawHourMarkers(center, radius, primaryColor)

    // Draw background arc (full circle)
    drawCircle(
        color = backgroundColor,
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Draw progress arc
    val sweepAngle = 360f * progress
    drawArc(
        color = progressColor,
        startAngle = -90f, // Start from top
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Draw clock hands based on remaining time
    drawClockHands(center, radius * 0.7f, progress, progressColor)

    // Draw center dot
    drawCircle(
        color = progressColor,
        radius = strokeWidth / 2,
        center = center
    )
}

/**
 * Draws hour markers around the clock face
 */
private fun DrawScope.drawHourMarkers(
    center: Offset,
    radius: Float,
    color: Color
) {
    for (i in 0 until 12) {
        val angle = (i * 30 - 90) * PI / 180 // Convert to radians, offset by 90 degrees
        val isMainHour = i % 3 == 0 // 12, 3, 6, 9 o'clock positions

        val startRadius = if (isMainHour) radius * 0.85f else radius * 0.9f
        val endRadius = radius * 0.95f
        val strokeWidth = if (isMainHour) 3.dp.toPx() else 1.5.dp.toPx()

        val startX = center.x + cos(angle).toFloat() * startRadius
        val startY = center.y + sin(angle).toFloat() * startRadius
        val endX = center.x + cos(angle).toFloat() * endRadius
        val endY = center.y + sin(angle).toFloat() * endRadius

        drawLine(
            color = color.copy(alpha = 0.6f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Draws clock hands representing the current time progress
 */
private fun DrawScope.drawClockHands(
    center: Offset,
    maxRadius: Float,
    progress: Float,
    color: Color
) {
    // Calculate angles for hands (progress goes from full to empty)
    val remainingProgress = 1f - progress
    val minuteAngle = (remainingProgress * 360f - 90f) // -90 to start from top
    val hourAngle = (remainingProgress * 30f - 90f) // Hour hand moves slower

    // Draw hour hand (shorter, thicker)
    rotate(degrees = hourAngle, pivot = center) {
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, center.y - maxRadius * 0.5f),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Draw minute hand (longer, thinner)
    rotate(degrees = minuteAngle, pivot = center) {
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, center.y - maxRadius * 0.8f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Digital time display overlay
 */
@Composable
private fun DigitalTimeDisplay(
    currentSeconds: Float,
    isBreak: Boolean
) {
    val seconds = ceil(currentSeconds / 1000).toInt()
    val timeText = formatTime(seconds)

    Text(
        text = timeText,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = if (isBreak) Color.White else Color.Black,
        modifier = Modifier.offset(y = 60.dp)
    )
}

/**
 * Formats seconds into MM:SS or SS format
 */
private fun formatTime(totalSeconds: Int): String {
    return if (totalSeconds >= 60) {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    } else {
        totalSeconds.toString()
    }
}

// Alternative minimal analog timer without clock hands
@Composable
fun MinimalAnalogTimer(
    currentSeconds: Float,
    totalSeconds: Float,
    modifier: Modifier = Modifier,
    isBreak: Boolean = false,
    size: Dp = 120.dp
) {
    val progress = if (totalSeconds > 0) currentSeconds / totalSeconds else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "minimal_timer_progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = minOf(size.width, size.height) / 2f - 20f
            val strokeWidth = 12.dp.toPx()

            val primaryColor = if (isBreak) Color.White else AzureBlue

            // Background circle
            drawCircle(
                color = primaryColor.copy(alpha = 0.2f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            if (progress > 0) {
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Central time display
        val seconds = ceil(currentSeconds / 1000).toInt()
        Text(
            text = formatTime(seconds),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isBreak) Color.White else Color.Black
        )
    }
}

// Preview composables
@Preview(showBackground = true)
@Composable
private fun AnalogTimerClockPreview() {
    AnalogTimerClock(
        currentSeconds = 45000f, // 45 seconds remaining
        totalSeconds = 60000f,   // 1 minute total
        isBreak = false
    )
}

@Preview(showBackground = true)
@Composable
private fun AnalogTimerClockBreakPreview() {
    AnalogTimerClock(
        currentSeconds = 5000f,  // 5 seconds remaining
        totalSeconds = 15000f,   // 15 seconds total
        isBreak = true
    )
}

@Preview(showBackground = true)
@Composable
private fun MinimalAnalogTimerPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        MinimalAnalogTimer(
            currentSeconds = 20000f,
            totalSeconds = 30000f,
            isBreak = false
        )
        MinimalAnalogTimer(
            currentSeconds = 5000f,
            totalSeconds = 10000f,
            isBreak = true
        )
    }
}
