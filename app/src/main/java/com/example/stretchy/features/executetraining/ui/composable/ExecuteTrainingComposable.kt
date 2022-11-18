package com.example.stretchy.features.executetraining.ui.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
import com.example.stretchy.theme.AzureBlue
import com.example.stretchy.theme.LapisLazuli
import kotlin.math.*

@Composable
fun ExecuteTrainingComposable(viewModel: ExecuteTrainingViewModel = viewModel()) {
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
                                    exerciseName = item.exerciseName,
                                    nextExerciseName = item.nextExercise,
                                    currentExerciseTime = item.currentTime,
                                    totalExerciseTime = item.totalExerciseTime,
                                    trainingProgressPercent = item.trainingProgressPercent
                                )
                            }
                            is ActivityItem.Break -> BreakComposable(
                                nextExerciseName = item.nextExercise,
                                currentTime = item.currentTime,
                                totalTime = item.totalExerciseTime,
                                trainingProgressPercent = item.trainingProgressPercent
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
    totalTime: Int,
    trainingProgressPercent: Int
) {
    var visible by remember { mutableStateOf(false) }
    Text(
        text = "Prepare to next Exercise:",
        fontSize = 16.sp,
        color = Color.LightGray,
        fontWeight = FontWeight.Bold
    )
    if (!visible) {
        Text(text = "", fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
    AnimatedVisibility(visible = visible, enter = fadeIn(initialAlpha = 0.3f)) {
        Text(text = nextExerciseName, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(100.dp))
    TimerComposable(
        totalSeconds = totalTime.toFloat() * 1000,
        modifier = Modifier.size(300.dp),
        currentSeconds = currentTime
    )
    Spacer(modifier = Modifier.height(77.dp))
    ProgressBarComposable(percentageOfTimer = trainingProgressPercent)
    visible = true
}

@Composable
fun ExerciseComposable(
    exerciseName: String,
    nextExerciseName: String?,
    currentExerciseTime: Float,
    totalExerciseTime: Int,
    trainingProgressPercent: Int
) {
    var visible by remember { mutableStateOf(false) }
    if (!visible) {
        Text(text = "", fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
    AnimatedVisibility(visible = visible, enter = fadeIn(initialAlpha = 0.3f)) {
        Text(text = exerciseName, fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(100.dp))
    TimerComposable(
        totalSeconds = totalExerciseTime.toFloat() * 1000,
        modifier = Modifier.size(300.dp),
        currentSeconds = currentExerciseTime
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
    if (!visible) {
        Text(text = "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
    AnimatedVisibility(visible = visible, enter = fadeIn(initialAlpha = 0.3f)) {
        Text(text = nextExerciseName ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
    ProgressBarComposable(percentageOfTimer = trainingProgressPercent)
    visible = true
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimerComposable(
    totalSeconds: Float,
    currentSeconds: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 10.dp
) {
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
        AnimatedContent(
            targetState = (ceil((currentSeconds) / 1000)).toInt().toString(),
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) with
                        fadeOut(animationSpec = tween(150))
            }
        ) { seconds ->
            Text(
                text = seconds,
                fontSize = 100.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun ProgressBarComposable(
    percentageOfTimer: Int,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 15.dp
) {
    val heightPos = 240f
    val startPos = -540f
    val progressBarStart = Offset(startPos, heightPos)
    Canvas(modifier = modifier) {
        drawLine(
            color = Color.LightGray,
            start = progressBarStart,
            end = Offset(-startPos, heightPos),
            strokeWidth = strokeWidth.toPx()
        )
        drawLine(
            color = Color(LapisLazuli.toArgb()),
            start = progressBarStart,
            end = Offset(
                (2 * -startPos * (percentageOfTimer.toFloat() / 100)) + startPos,
                heightPos
            ),
            strokeWidth = strokeWidth.toPx()
        )
    }
}