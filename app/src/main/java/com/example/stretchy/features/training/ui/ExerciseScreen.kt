package com.example.stretchy.navigation.screens

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
import com.example.stretchy.ExerciseViewModel
import com.example.stretchy.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stretchy.R
import com.example.stretchy.ui.theme.data.Break
import com.example.stretchy.ui.theme.data.Exercise
import kotlin.math.*

@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel = viewModel()) {
    Surface(modifier = Modifier
        .fillMaxSize()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            viewModel.stopTimer()
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
                    is ExerciseViewModel.ExerciseUiState.Loading ->
                        Text(
                            text = stringResource(id = R.string.loading),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    is ExerciseViewModel.ExerciseUiState.Success -> {
                        when (val item = state.data.activityItem) {
                            is Exercise -> {
                                ExerciseComposable(
                                    exerciseName = item.exerciseName,
                                    nextExerciseName = item.nextExercise,
                                    currentTime = item.currentTime,
                                    totalTime = item.totalTime
                                )
                            }
                            is Break -> ExerciseComposable(
                                exerciseName = stringResource(id = R.string.exercise_break),
                                nextExerciseName = item.nextExercise,
                                currentTime = item.currentTime,
                                totalTime = item.totalTime
                            )
                        }
                    }
                    ExerciseViewModel.ExerciseUiState.Error -> Text(
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
fun ExerciseComposable(
    exerciseName: String,
    nextExerciseName: String,
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
    Text(
        text = stringResource(id = R.string.nxt_exercise),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.LightGray
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = nextExerciseName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
}

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
                color = Color(azureBlue.toArgb()),
                startAngle = -215f,
                sweepAngle = sweepAngle * percentageOfTimer,
                useCenter = false,
                size = Size(size.width, size.height),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = (ceil((currentSeconds) / 1000)).toInt().toString(),
            fontSize = 100.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}