package com.example.stretchy.features.createtraining.ui.composable

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
import com.example.stretchy.features.createtraining.ui.composable.buttons.CreateOrEditTrainingButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.TrainingName
import com.example.stretchy.features.createtraining.ui.composable.list.ExerciseList

@Composable
fun CreateTrainingComposable(
    navController: NavController,
    viewModel: CreateOrEditTrainingViewModel
) {
    var trainingName: String by remember { mutableStateOf("") }
    var trainingId: Long? by remember { mutableStateOf(null) }
    var isTrainingBeingEdited by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            Modifier
                .padding(top = 16.dp)
        ) {
            when (val state = viewModel.uiState.collectAsState().value) {
                is CreateTrainingUiState.Success -> {
                    trainingId = state.trainingId
                    trainingName = state.currentName
                    TrainingName(viewModel, trainingName)
                    AutoBreakCheckbox(viewModel = viewModel)
                    isTrainingBeingEdited = state.editingTraining
                    Spacer(modifier = Modifier.height(24.dp))
                    ExerciseList(
                        exercises = state.activities,
                        viewModel = viewModel,
                        trainingType = state.trainingType,
                        isAutoBreakClicked = state.isAutomaticBreakButtonClicked
                    )
                }
                is CreateTrainingUiState.Error -> {
                    HandleError(state = state, context = context)
                }
                is CreateTrainingUiState.Done -> {
                    if (navController.currentDestination?.route != Screen.TrainingListScreen.route) {
                        navController.navigate(Screen.TrainingListScreen.route)
                    }
                }
                CreateTrainingUiState.Init -> {}
            }
        }
        Spacer(modifier = Modifier.height(200.dp))
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            CreateOrEditTrainingButton(viewModel, isTrainingBeingEdited, navController, trainingId)
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

@Composable
private fun AutoBreakCheckbox(viewModel: CreateOrEditTrainingViewModel) {
    var isAutoBreakChecked by remember { mutableStateOf(true) }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isAutoBreakChecked,
            onCheckedChange = {
                isAutoBreakChecked = it
                if (isAutoBreakChecked) {
                    viewModel.enableAutoBreaks()
                } else {
                    viewModel.disableAutoBreaks()
                }
            },
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .weight(1f),
            text = "auto breaks",
            fontSize = 16.sp,

            fontWeight = FontWeight.Bold
        )
    }
}
