package com.example.stretchy.features.createtraining.ui.composable

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
import com.example.stretchy.features.createtraining.ui.composable.buttons.AutoBreakCheckbox
import com.example.stretchy.features.createtraining.ui.composable.buttons.CreateOrEditTrainingButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.TrainingName
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.list.RecyclerView
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity

@Composable
fun CreateTrainingComposable(
    navController: NavController,
    viewModel: CreateOrEditTrainingViewModel
) {
    var trainingName: String by remember { mutableStateOf("") }
    var trainingId: Long? by remember { mutableStateOf(null) }
    var isTrainingBeingEdited by remember { mutableStateOf(false) }
    var isListInitialized by remember { mutableStateOf(false) }
    var isAutoBreakButtonClicked by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var exerciseList: List<ExercisesWithBreaks> by remember {
        mutableStateOf(mutableListOf())
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val newList = exerciseList.toMutableList()
                newList.add(
                    ExercisesWithBreaks(
                        exerciseList.lastIndex + 1,
                        Exercise(),
                        if (isAutoBreakButtonClicked) viewModel.getAutoBreakDuration() else 0,
                        isExpanded = true
                    )
                )
                exerciseList = newList
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.desc_plus_icon)
                )
            }
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(top = 16.dp, bottom = 64.dp)
            ) {
                when (val state = viewModel.uiState.collectAsState().value) {
                    is CreateTrainingUiState.Success -> {
                        trainingId = state.trainingId
                        trainingName = state.currentName
                        TrainingName(viewModel, trainingName)
                        AutoBreakCheckbox(viewModel = viewModel)
                        isTrainingBeingEdited = state.editingTraining
                        isAutoBreakButtonClicked = state.isAutomaticBreakButtonClicked
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!isListInitialized) {
                            exerciseList = state.activities.toExerciseWithBreaks()
                            isListInitialized = true
                        }
                        RecyclerView(
                            activitiesWithoutBreaks = exerciseList,
                            onListChange = { exerciseList = it }
                        )
                    }
                    is CreateTrainingUiState.Error -> {
                        HandleError(state = state, context = context)
                    }
                    is CreateTrainingUiState.Done -> {
                        if (navController.currentDestination?.route != Screen.StretchingListScreen.route) {
                            navController.navigate(Screen.StretchingListScreen.route)
                        }
                    }
                    CreateTrainingUiState.Init -> {}
                }
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                CreateOrEditTrainingButton(
                    viewModel,
                    isTrainingBeingEdited,
                    trainingId,
                    exerciseList
                )
            }
        }
    }
}

@Composable
private fun HandleError(state: CreateTrainingUiState.Error, context: Context) {
    when (state.reason) {
        CreateTrainingUiState.Error.Reason.MissingTrainingName -> {
            Toast.makeText(
                context,
                R.string.specify_training_name,
                Toast.LENGTH_LONG
            ).show()
        }
        CreateTrainingUiState.Error.Reason.NotEnoughExercises -> {
            Toast.makeText(
                context,
                R.string.add_min_2_exercises,
                Toast.LENGTH_LONG
            ).show()
        }
        is CreateTrainingUiState.Error.Reason.Unknown -> {
            Toast.makeText(
                context,
                R.string.something_went_wrong,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

private fun List<Activity>.toExerciseWithBreaks(): List<ExercisesWithBreaks> {
    val list = mutableListOf<ExercisesWithBreaks>()
    this.forEachIndexed() { index, item ->
        if (item.activityType != ActivityType.BREAK) {
            list.add(
                ExercisesWithBreaks(
                    list.lastIndex + 1,
                    Exercise(
                        item.activityId.toInt(),
                        item.name,
                        item.activityOrder,
                        item.duration
                    ),
                    if (this.getOrNull(index + 1)?.activityType == ActivityType.BREAK) this.getOrNull(
                        index + 1
                    )?.duration
                    else null, false
                )
            )
        }
    }
    return list
}