package com.example.stretchy.features.executetraining.ui.composable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.features.executetraining.sound.managers.consumeSoundEvents
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.components.AnimatedTrainingProgressBar
import com.example.stretchy.features.executetraining.ui.composable.components.QuittingSnackbar
import com.example.stretchy.features.executetraining.ui.composable.timer.AnalogTimerClock
import com.example.stretchy.features.executetraining.ui.data.DisplayableActivityItem
import com.example.stretchy.database.data.ActivityType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape

@Composable
fun ExecuteTrainingScreenn(
    viewModel: ExecuteTrainingViewModel,
    soundPlayer: SoundPlayer,
    navController: NavController
) {
    var showSnackbar by remember { mutableStateOf(false) }
    var numberOfBackButtonClick by remember { mutableStateOf(0) }

    BackHandler {
        numberOfBackButtonClick++
        showSnackbar = true
        if (numberOfBackButtonClick == 2) {
            navController.popBackStack()
        }
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.uiState.collectAsState().value

    consumeSoundEvents(state.soundEvent, coroutineScope, soundPlayer, context)

    Scaffold(scaffoldState = scaffoldState) { padding ->
        if (showSnackbar) {
            QuittingSnackbar(
                scaffoldState = scaffoldState,
                navController = navController,
                onDismiss = {
                    showSnackbar = false
                    numberOfBackButtonClick = 0
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.toggleStartStopTimer()
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    LoadingState()
                }
                state.error != null -> {
                    ErrorState()
                }
                state.trainingCompleted != null -> {
                    TrainingCompletedState(
                        trainingCompleted = state.trainingCompleted,
                        navController = navController
                    )
                }
                state.displayableActivityItemListWithBreakMerged != null -> {
                    TrainingActiveState(
                        state = state
                    )
                }
            }

            // Progress bar at bottom
            if (state.displayableActivityItemListWithBreakMerged != null && state.trainingCompleted == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AnimatedTrainingProgressBar(percentage = state.trainingProgressPercent)
                }
            }
        }
    }
}

@Composable
fun TextSpacer(fontSize: TextUnit) {
    Text("", fontSize = fontSize)
}

@Composable
private fun LoadingState() {
    Text(
        text = stringResource(id = R.string.loading),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ErrorState() {
    Text(
        text = stringResource(id = R.string.error),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun TrainingCompletedState(
    trainingCompleted: com.example.stretchy.features.executetraining.ui.data.TrainingCompleted,
    navController: NavController
) {
    TrainingSummaryVieww(
        numberOfExercises = trainingCompleted.numberOfExercises,
        timeSpent = trainingCompleted.currentTrainingTime,
        navController = navController
    )
}

@Composable
private fun TrainingActiveState(
    state: com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
) {
    val currentPage = state.currentDisplayPage
    val currentItem = state.displayableActivityItemListWithBreakMerged?.getOrNull(currentPage)
    val currentActivityType = state.activityTypes?.getOrNull(currentPage)

    if (currentItem == null || currentActivityType == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section with title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            when (currentActivityType) {
                ActivityType.BREAK -> {
                    BreakTitle(
                        nextExerciseName = currentItem.exercise.nextExercise ?: ""
                    )
                }
                ActivityType.TIMELESS_EXERCISE -> {
                    val exercise = currentItem.exercise as DisplayableActivityItem.TimelessExercise
                    TimelessExerciseTitle(
                        exerciseName = exercise.name
                    )
                }
                else -> {
                    val exercise = currentItem.exercise as DisplayableActivityItem.Exercise
                    ExerciseTitle(
                        exerciseName = exercise.name
                    )
                }
            }
        }

        // Center section with timer
        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.Center
        ) {
            when (currentActivityType) {
                ActivityType.TIMELESS_EXERCISE -> {
                    TimelessExerciseIndicator()
                }
                else -> {
                    AnalogTimerClock(
                        timeRemaining = state.currentSeconds,
                        totalTime = currentItem.exercise.totalExerciseTime.toFloat() * 1000,
                        isBreak = currentActivityType == ActivityType.BREAK,
                        modifier = Modifier.size(280.dp)
                    )
                }
            }
        }

        // Bottom section with next exercise info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            NextExerciseInfo(
                nextExerciseName = currentItem.exercise.nextExercise,
                currentActivityType = currentActivityType
            )
        }
    }
}

@Composable
private fun ExerciseTitle(exerciseName: String) {
    Text(
        text = exerciseName,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color.Black
    )
}

@Composable
private fun BreakTitle(nextExerciseName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.prepare_next_exercise),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = nextExerciseName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}

@Composable
private fun TimelessExerciseTitle(exerciseName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = exerciseName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap when ready to continue",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TimelessExerciseIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▶",
                fontSize = 72.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun NextExerciseInfo(
    nextExerciseName: String?,
    currentActivityType: ActivityType
) {
    if (!nextExerciseName.isNullOrBlank() && currentActivityType != ActivityType.BREAK) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.nxt_exercise),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nextExerciseName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    }
}

@Preview(name = "Execute Training - Preview", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ExecuteTrainingScreenPreview() {
    // Mock exercise item
    val mockExercise = DisplayableActivityItem.Exercise(
        name = "Push-ups",
        nextExercise = "Squats",
        currentTime = 15000f,
        totalExerciseTime = 30
    )
    val mockItem = com.example.stretchy.features.executetraining.ui.data.ActivityItemExerciseAndBreakMerged(
        exercise = mockExercise,
        breakItem = null
    )

    val mockState = com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState(
        isLoading = false,
        error = null,
        displayableActivityItemListWithBreakMerged = listOf(mockItem),
        trainingCompleted = null,
        currentSeconds = 15000f,
        trainingProgressPercent = 0.25f,
        activityTypes = listOf(com.example.stretchy.database.data.ActivityType.EXERCISE),
        currentDisplayPage = 0,
        soundEvent = null
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
