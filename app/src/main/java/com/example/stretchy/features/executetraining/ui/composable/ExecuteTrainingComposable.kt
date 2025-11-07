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
import com.example.stretchy.features.executetraining.ui.composable.timer.TimerTheme

// View State definitions - simplified for clean View layer
sealed class ExecuteTrainingViewState {
    object Loading : ExecuteTrainingViewState()
    object Error : ExecuteTrainingViewState()
    data class Completed(
        val numberOfExercises: Int,
        val timeSpent: String,
        val onNavigateBack: () -> Unit
    ) : ExecuteTrainingViewState()
    data class Active(
        val title: String,
        val subtitle: String?,
        val timeRemaining: Float,
        val totalTime: Float,
        val timerTheme: TimerTheme,
        val isBreak: Boolean,
        val showTimer: Boolean,
        val nextExercise: String?,
        val progressPercent: Float
    ) : ExecuteTrainingViewState()
}

data class ViewData(
    val title: String,
    val subtitle: String?,
    val timeRemaining: Float,
    val totalTime: Float,
    val timerTheme: TimerTheme,
    val isBreak: Boolean,
    val showTimer: Boolean,
    val nextExercise: String?
)

private fun prepareViewData(
    currentItem: com.example.stretchy.features.executetraining.ui.data.ActivityItemExerciseAndBreakMerged,
    currentActivityType: ActivityType,
    currentSeconds: Float
): ViewData {
    return when (currentActivityType) {
        ActivityType.BREAK -> {
            ViewData(
                title = "Get Ready",
                subtitle = currentItem.exercise.nextExercise ?: "",
                timeRemaining = currentSeconds,
                totalTime = currentItem.exercise.totalExerciseTime.toFloat() * 1000,
                timerTheme = TimerTheme.TRAINING, // Use Training theme for breaks as requested
                isBreak = true,
                showTimer = true,
                nextExercise = null
            )
        }
        ActivityType.TIMELESS_EXERCISE -> {
            val exercise = currentItem.exercise as DisplayableActivityItem.TimelessExercise
            ViewData(
                title = exercise.name,
                subtitle = "Tap when ready to continue",
                timeRemaining = 0f,
                totalTime = 0f,
                timerTheme = TimerTheme.TRAINING, // Default to training
                isBreak = false,
                showTimer = false,
                nextExercise = exercise.nextExercise
            )
        }
        else -> {
            val exercise = currentItem.exercise as DisplayableActivityItem.Exercise
            ViewData(
                title = exercise.name,
                subtitle = null,
                timeRemaining = currentSeconds,
                totalTime = exercise.totalExerciseTime.toFloat() * 1000,
                timerTheme = TimerTheme.TRAINING, // Default to training, can be changed based on exercise type
                isBreak = false,
                showTimer = true,
                nextExercise = exercise.nextExercise
            )
        }
    }
}

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
                    ExecuteTrainingView(
                        viewState = ExecuteTrainingViewState.Loading
                    )
                }
                state.error != null -> {
                    ExecuteTrainingView(
                        viewState = ExecuteTrainingViewState.Error
                    )
                }
                state.trainingCompleted != null -> {
                    ExecuteTrainingView(
                        viewState = ExecuteTrainingViewState.Completed(
                            numberOfExercises = state.trainingCompleted.numberOfExercises,
                            timeSpent = state.trainingCompleted.currentTrainingTime,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    )
                }
                state.displayableActivityItemListWithBreakMerged != null -> {
                    // Extract data from state and prepare for view
                    val currentPage = state.currentDisplayPage
                    val currentItem = state.displayableActivityItemListWithBreakMerged.getOrNull(currentPage)
                    val currentActivityType = state.activityTypes?.getOrNull(currentPage)

                    if (currentItem != null && currentActivityType != null) {
                        val viewData = prepareViewData(currentItem, currentActivityType, state.currentSeconds)

                        ExecuteTrainingView(
                            viewState = ExecuteTrainingViewState.Active(
                                title = viewData.title,
                                subtitle = viewData.subtitle,
                                timeRemaining = viewData.timeRemaining,
                                totalTime = viewData.totalTime,
                                timerTheme = viewData.timerTheme,
                                isBreak = viewData.isBreak,
                                showTimer = viewData.showTimer,
                                nextExercise = viewData.nextExercise,
                                progressPercent = state.trainingProgressPercent
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextSpacer(fontSize: TextUnit) {
    Text("", fontSize = fontSize)
}

/**
 * Pure View Component - receives prepared data and renders UI
 * No business logic, no state extraction - just presentation
 */
@Composable
fun ExecuteTrainingView(viewState: ExecuteTrainingViewState) {
    when (viewState) {
        is ExecuteTrainingViewState.Loading -> {
            LoadingView()
        }
        is ExecuteTrainingViewState.Error -> {
            ErrorView()
        }
        is ExecuteTrainingViewState.Completed -> {
            CompletedView(
                numberOfExercises = viewState.numberOfExercises,
                timeSpent = viewState.timeSpent,
                onNavigateBack = viewState.onNavigateBack
            )
        }
        is ExecuteTrainingViewState.Active -> {
            ActiveTrainingView(
                title = viewState.title,
                subtitle = viewState.subtitle,
                timeRemaining = viewState.timeRemaining,
                totalTime = viewState.totalTime,
                timerTheme = viewState.timerTheme,
                isBreak = viewState.isBreak,
                showTimer = viewState.showTimer,
                nextExercise = viewState.nextExercise,
                progressPercent = viewState.progressPercent
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Text(
        text = stringResource(id = R.string.loading),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ErrorView() {
    Text(
        text = stringResource(id = R.string.error),
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun CompletedView(
    numberOfExercises: Int,
    timeSpent: String,
    onNavigateBack: () -> Unit
) {
    // Custom completion view that uses callback instead of NavController
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8DC)), // Light yellow background like TrainingSummary
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.you_finished_training),
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(id = R.string.total_exercises, numberOfExercises),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.time_spent, timeSpent),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Finish button
            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigateBack() }
                    .background(
                        Color.Black,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 48.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.finish),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Active Training View - Clean 3-section layout with injected values
 */
@Composable
private fun ActiveTrainingView(
    title: String,
    subtitle: String?,
    timeRemaining: Float,
    totalTime: Float,
    timerTheme: TimerTheme,
    isBreak: Boolean,
    showTimer: Boolean,
    nextExercise: String?,
    progressPercent: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section - Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            TitleSection(
                title = title,
                subtitle = subtitle
            )
        }

        // Center section - Timer or Indicator
        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.Center
        ) {
            if (showTimer) {
                AnalogTimerClock(
                    timeRemaining = timeRemaining,
                    totalTime = totalTime,
                    theme = timerTheme,
                    isBreak = isBreak,
                    modifier = Modifier.size(280.dp)
                )
            } else {
                TimelessExerciseIndicator()
            }
        }

        // Bottom section - Next exercise + Progress bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            NextExerciseSection(nextExercise = nextExercise, showForBreak = isBreak)

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedTrainingProgressBar(percentage = progressPercent)
        }
    }
}

@Composable
private fun TitleSection(title: String, subtitle: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (subtitle != null) {
            Text(
                text = stringResource(id = R.string.prepare_next_exercise),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        } else {
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
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
private fun NextExerciseSection(nextExercise: String?, showForBreak: Boolean) {
    if (!nextExercise.isNullOrBlank() && !showForBreak) {
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
                text = nextExercise,
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

    ExecuteTrainingView(
        viewState = ExecuteTrainingViewState.Active(
            title = "Push-ups",
            subtitle = null,
            timeRemaining = 15000f,
            totalTime = 30000f,
            timerTheme = TimerTheme.TRAINING,
            isBreak = false,
            showTimer = true,
            nextExercise = "Squats",
            progressPercent = 0.25f
        )
    )
}
