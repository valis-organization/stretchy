package com.example.stretchy.features.executetraining.ui.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
import com.example.stretchy.theme.AzureBlue
import com.example.stretchy.theme.LapisLazuli
import kotlin.math.ceil

@Composable
fun ExecuteTrainingComposable(
    viewModel: ExecuteTrainingViewModel,
    navController: NavController
) {
    var disableExerciseAnimation =
        true //The first exercise cannot be animated because at the beginning of training the timer is stopped
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                viewModel.toggleStartStopTimer()
            },
            contentAlignment = Alignment.Center
        )
        {
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
                                totalTime = item.totalExerciseTime,
                                trainingProgressPercent = item.trainingProgressPercent,
                                disableExerciseAnimation = disableExerciseAnimation
                            )
                            disableExerciseAnimation = false
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
                is ExecuteTrainingUiState.TrainingCompleted -> TrainingSummaryComposable(
                    numberOfExercises = state.numberOfExercises,
                    currentTrainingTime = state.currentTrainingTime,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun BreakComposable(
    nextExerciseName: String,
    currentTime: Float,
    totalTime: Int,
    trainingProgressPercent: Float
) {
    var showAnimation by remember { mutableStateOf(false) } //if set to true fade-in animations appear
    Column(
        verticalArrangement = Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Prepare to next Exercise:",
            fontSize = 16.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Bold
        )
        if (!showAnimation) {  //prevents "screen jumping" due to invisible texts
            TextSpacer(fontSize = 32.sp)
        }
        AnimatedVisibility(
            visible = showAnimation,
            enter = textFadeInProperties()
        ) {
            Text(text = nextExerciseName, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(100.dp))
        TimerComposable(
            totalSeconds = totalTime.toFloat() * 1000,
            modifier = Modifier.size(300.dp),
            currentSeconds = currentTime
        )
        Spacer(modifier = Modifier.height(44.dp))
        TextSpacer(fontSize = 40.sp)
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        AnimatedTrainingProgressBar(percentage = trainingProgressPercent)
    }
    showAnimation = true
}

@Composable
fun ExerciseComposable(
    exerciseName: String,
    nextExerciseName: String?,
    currentTime: Float,
    totalTime: Int,
    trainingProgressPercent: Float,
    disableExerciseAnimation: Boolean
) {
    var showAnimation by remember { mutableStateOf(disableExerciseAnimation) } //if set to true fade-in animations appear
    Column(
        verticalArrangement = Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextSpacer(fontSize = 16.sp)
        if (!showAnimation) {
            TextSpacer(fontSize = 32.sp)
        }
        AnimatedVisibility(
            visible = showAnimation,
            enter = textFadeInProperties()
        ) {
            Text(text = exerciseName, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
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
        } else {
            TextSpacer(fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (!showAnimation) {
            TextSpacer(fontSize = 24.sp)
        }
        AnimatedVisibility(
            visible = showAnimation,
            enter = textFadeInProperties()
        ) {
            Text(text = nextExerciseName ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        TrainingProgressBar(percentage = trainingProgressPercent)
    }
    showAnimation = true
}

@OptIn(ExperimentalAnimationApi::class)
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

        AnimatedContent(
            targetState = sec.toString(),
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) with
                        fadeOut(animationSpec = tween(150))
            }
        ) { seconds ->
            Text(
                text = toCounterText(seconds.toInt()),
                fontSize = 100.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun AnimatedTrainingProgressBar(percentage: Float) {
    val initialProgressBarPosition =
        1f * percentage //0f - start of the progress bar, 1f - target position
    val animateLine =
        remember { androidx.compose.animation.core.Animatable(initialProgressBarPosition) }

    LaunchedEffect(animateLine) {
        animateLine.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
        )
    }
    TrainingProgressBar(percentage = percentage, progressBarFillingAmount = animateLine.value)
}

@Composable
fun TrainingProgressBar(
    progressBarFillingAmount: Float = 1f, // Used for animations
    percentage: Float
) {
    val thickness = 16.dp
    Canvas(Modifier.fillMaxWidth()) {
        drawLine(
            color = Color(LapisLazuli.toArgb()),
            start = Offset.Zero,
            end = Offset(progressBarFillingAmount * (size.width * percentage), 0f),
            strokeWidth = thickness.toPx()
        )
    }
}

@Composable
fun TextSpacer(fontSize: TextUnit) {
    Text("", fontSize = fontSize)
}

private fun textFadeInProperties(): EnterTransition {
    return fadeIn(initialAlpha = 0f, animationSpec = tween(500))
}
