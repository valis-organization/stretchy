package com.example.stretchy.features.executetraining.ui.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
import com.example.stretchy.theme.AzureBlue
import kotlin.math.*

@Composable
fun ExecuteTrainingComposable(
    viewModel: ExecuteTrainingViewModel,
    trainingId: String,
) {
    viewModel.init(trainingId.toLong())
    Surface(modifier = Modifier
        .fillMaxSize()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            viewModel.toggleStartStopTimer()
        }) {
        Box(
            contentAlignment = Alignment.Center
        )
        {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = viewModel.uiState.collectAsState().value) {
                    is ExecuteTrainingUiState.Loading ->
                        Text(
                            text = stringResource(id = R.string.loading),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    is ExecuteTrainingUiState.Success -> {
                        when (val item = state.activityItem) {
                            is ActivityItem.Exercise -> {
                                ExerciseComposable(
                                    exerciseName = item.name,
                                    nextExerciseName = item.nextExercise,
                                    currentTime = item.currentTime,
                                    totalTime = item.totalTime
                                )
                            }
                            is ActivityItem.Break -> BreakComposable(
                                nextExerciseName = item.nextExercise,
                                currentTime = item.currentTime,
                                totalTime = item.totalTime
                            )
                        }
                    }
                    ExecuteTrainingUiState.Error -> Text(
                        text = stringResource(id = R.string.error),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BreakComposable(
    nextExerciseName: String,
    currentTime: Float,
    totalTime: Int
) {
    Text(
        text = "Prepare to next Exercise:",
        fontSize = 16.sp,
        color = Color.LightGray,
        fontWeight = FontWeight.Bold
    )
    Text(text = nextExerciseName, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(100.dp))
    TimerComposable(
        totalSeconds = totalTime.toFloat() * 1000,
        modifier = Modifier.size(300.dp),
        currentSeconds = currentTime
    )
}

@Composable
fun ExerciseComposable(
    exerciseName: String,
    nextExerciseName: String?,
    currentTime: Float,
    totalTime: Int
) {
    Text(text = exerciseName, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(100.dp))
    TimerComposable(
        totalSeconds = totalTime.toFloat() * 1000,
        modifier = Modifier.size(300.dp),
        currentSeconds = currentTime
    )
    Spacer(modifier = Modifier.height(36.dp))
    if (!nextExerciseName.isNullOrBlank()) {
        Text(
            text = stringResource(id = R.string.nxt_exercise),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = nextExerciseName ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun TimerComposable(
    totalSeconds: Float,
    currentSeconds: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 10.dp
) {
    fun toCounterText(seconds: Int): String {
        return if (seconds <= 59) {
            seconds.toString()
        } else {
            val minutes = seconds / 60
            val s = seconds % 60
            val secStr = if (s <= 9) {
                "0$s"
            } else {
                s
            }
            "$minutes:$secStr"
        }
    }

    val percentageOfTimer = (currentSeconds / totalSeconds)
    val sweepAngle = 250f

    Box(contentAlignment = Alignment.Center)
    {
        Canvas(modifier = modifier) {
            drawArc(
                color = Color(AzureBlue.toArgb()),
                startAngle = -215f,
                sweepAngle = sweepAngle * percentageOfTimer,
                useCenter = false,
                size = Size(size.width, size.height),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        val sec = (ceil((currentSeconds) / 1000)).toInt()

        Text(
            text = toCounterText(sec),
            fontSize = 100.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}