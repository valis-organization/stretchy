package com.example.stretchy.features.executetraining.ui.composable.timer

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.theme.AzureBlue
import kotlin.math.*

/**
 * Analog timer clock component that displays time in a circular format
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnalogTimerClock(
    modifier: Modifier = Modifier,
    timeRemaining: Float,
    totalTime: Float = timeRemaining,
    isBreak: Boolean = false,
    strokeWidth: Float = 10f
) {

    fun formatTime(millis: Float): String {
        val seconds = (millis / 1000).toInt()
        return if (seconds <= 59) {
            seconds.toString()
        } else {
            val minutes = seconds / 60
            val s = seconds % 60
            val secStr = if (s <= 9) "0$s" else s.toString()
            "$minutes:$secStr"
        }
    }

    val progress = if (totalTime > 0) timeRemaining / totalTime else 0f
    val sweepAngle = 360f * progress

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAnalogClock(
                progress = progress,
                sweepAngle = sweepAngle,
                isBreak = isBreak,
                strokeWidth = strokeWidth,
                size = size
            )
        }

        val seconds = (timeRemaining / 1000).toInt()
        AnimatedContent(
            targetState = seconds,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) with
                        fadeOut(animationSpec = tween(150))
            }
        ) { animatedSeconds ->
            Text(
                text = formatTime(animatedSeconds.toFloat() * 1000),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (isBreak) Color.White else Color.Black
            )
        }
    }
}

private fun DrawScope.drawAnalogClock(
    progress: Float,
    sweepAngle: Float,
    isBreak: Boolean,
    strokeWidth: Float,
    size: androidx.compose.ui.geometry.Size
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = minOf(size.width, size.height) / 2 - strokeWidth / 2

    // Draw background circle
    drawCircle(
        color = Color.LightGray.copy(alpha = 0.3f),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Draw progress arc
    drawArc(
        color = if (isBreak) Color.White else AzureBlue,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        topLeft = Offset(center.x - radius, center.y - radius),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Draw hour markers
    for (i in 0..11) {
        val angle = i * 30f - 90f
        val startRadius = radius - strokeWidth * 2
        val endRadius = radius - strokeWidth / 2
        val startX = center.x + startRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val startY = center.y + startRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
        val endX = center.x + endRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val endY = center.y + endRadius * sin(Math.toRadians(angle.toDouble())).toFloat()

        drawLine(
            color = Color.Gray,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

@Preview(name = "Analog Timer Clock", showBackground = true)
@Composable
private fun AnalogTimerClockPreview() {
    AnalogTimerClock(
        modifier = Modifier.size(200.dp),
        timeRemaining = 15000f,
        totalTime = 30000f,
        isBreak = false,
        strokeWidth = 10f
    )
}

