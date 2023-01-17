package com.example.stretchy.features.createtraining.ui.composable

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.*
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.features.createtraining.ui.list.DragDropLazyList
import com.example.stretchy.repository.Activity
import com.example.stretchy.theme.BananaMania
import com.example.stretchy.theme.WhiteSmoke
import java.util.*

@Composable
fun CreateTrainingComposable(
    navController: NavController,
    viewModel: CreateTrainingViewModel
) {
    var trainingName: String by remember { mutableStateOf("") }
    var trainingId: Long by remember { mutableStateOf(-1) }

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
                    TrainingName(viewModel, trainingName)
                    Spacer(modifier = Modifier.height(24.dp))
                    ExerciseList(exercises = state.activities, viewModel = viewModel)
                }
                is CreateTrainingUiState.Editing -> {
                    trainingId = state.trainingId
                    trainingName = state.trainingName
                    TrainingName(viewModel, trainingName)
                    Spacer(modifier = Modifier.height(24.dp))
                    ExerciseList(exercises = state.activities, viewModel = viewModel)
                }
                else -> {
                    TrainingName(viewModel, trainingName)
                    Spacer(modifier = Modifier.height(24.dp))
                    ExerciseList(emptyList(), viewModel = viewModel)
                }
            }
        }
        Spacer(modifier = Modifier.height(200.dp))
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    if (isTrainingBeingEdited(trainingId)) {
                        viewModel.editTraining(trainingId = trainingId)
                    } else {
                        viewModel.createTraining()
                    }
                    navController.navigate(Screen.TrainingListScreen.route)
                }
            ) {
                if (isTrainingBeingEdited(trainingId)) {
                    Text(stringResource(id = R.string.save_changes))
                } else {
                    Text(stringResource(id = R.string.create_training))
                }
            }
        }
    }
}

@Composable
fun ExerciseList(exercises: List<Activity>, viewModel: CreateTrainingViewModel) {
    var editedExercise by remember { mutableStateOf(Exercise()) }
    var widgetVisible by remember { mutableStateOf(false) }
    DragDropLazyList(
        modifier = Modifier.heightIn(0.dp, 240.dp),
        items = exercises,
        onSwap = viewModel::swapExercises
    ) { index, item ->
        Box(
            Modifier
                .fillMaxSize()
                .height(64.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    editedExercise = Exercise(item.name, item.duration, index)
                    widgetVisible = true
                }) {
            SwipeableExerciseItem(
                vm = viewModel,
                exercise = Exercise(item.name, item.duration, index)
            )
        }
    }
    CreateExerciseWidget(
        viewModel = viewModel,
        editedExercise = editedExercise,
        widgetVisible = widgetVisible,
        onAddClick = {
            widgetVisible = !widgetVisible
            editedExercise = Exercise()
        })
}

@Composable
fun CreateExerciseWidget(
    viewModel: CreateTrainingViewModel,
    editedExercise: Exercise,
    widgetVisible: Boolean,
    onAddClick: () -> Unit
) {
    val sliderMinValue = 10
    val sliderMaxValue = 300
    var exerciseDuration: Int by remember { mutableStateOf(sliderMinValue) }
    var exerciseName: String by remember { mutableStateOf("") }
    val context = LocalContext.current
    val exerciseIsBeingEdited: Boolean = editedExercise.name != ""

    exerciseName = editedExercise.name
    exerciseDuration = editedExercise.duration

    AnimatedVisibility(visible = !widgetVisible) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(BananaMania.toArgb()))
                .clickable {
                    onAddClick()
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.desc_plus_icon)
            )
        }
    }
    AnimatedVisibility(
        visible = widgetVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Column(
            Modifier
                .padding(start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(BananaMania.toArgb()))
                .padding(start = 12.dp, end = 12.dp)
        ) {
            ExerciseNameControls(currentName = exerciseName, onNameEntered = { exerciseName = it })
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = stringResource(R.string.duration) + " ${toDisplayableLength(exerciseDuration)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = exerciseDuration.toFloat(),
                onValueChange = {
                    exerciseDuration = it.toInt()
                },
                valueRange = 10f..sliderMaxValue.toFloat(),
            )
            AddOrSubtractButtons { changeValue ->
                if (exerciseDuration + changeValue in 10..300) {
                    exerciseDuration += changeValue
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 16.dp),
                onClick = {
                    if (exerciseName.isNotEmpty() && exerciseDuration != 0) {
                        onAddClick()
                        if (exerciseIsBeingEdited) {
                            viewModel.editActivity(
                                Activity(
                                    exerciseName,
                                    exerciseDuration,
                                    ActivityType.STRETCH
                                ), editedExercise.listId!!
                            )
                            Toast.makeText(context, R.string.exercise_edited, Toast.LENGTH_LONG)
                                .show()
                        } else {
                            viewModel.addActivity(
                                Activity(
                                    exerciseName,
                                    exerciseDuration,
                                    ActivityType.STRETCH
                                )
                            )
                            Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            R.string.specify_exercise_name,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                if (exerciseIsBeingEdited) {
                    Text(text = stringResource(id = R.string.save_changes))
                } else {
                    Text(text = stringResource(id = R.string.add_exercise))
                }
            }
        }
    }
}

@Composable
fun AddOrSubtractButtons(onTextEntered: (value: Int) -> Unit) {
    val modifier = Modifier
        .width(44.dp)
        .padding(end = 6.dp)
    Row {
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(-10) })
        {
            Text(text = "-10")
        }
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(-5) })
        {
            Text(text = "-5")
        }
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(-1) })
        {
            Text(text = "-1")
        }
        Spacer(modifier = Modifier.width(52.dp))
        Button(modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(+1) })
        {
            Text(text = "+1")
        }
        Button(
            modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(+5) })
        {
            Text(text = "+5")
        }
        Button(
            modifier = modifier,
            contentPadding = PaddingValues(0.dp),
            onClick = { onTextEntered(+10) })
        {
            Text(text = "+10")
        }
    }
}

@Composable
fun TrainingName(viewModel: CreateTrainingViewModel, initialTrainingName: String) {
    var trainingName by remember { mutableStateOf(initialTrainingName) }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 16.dp, end = 16.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        ),
        label = { Text(stringResource(id = R.string.training_name)) },
        value = trainingName,
        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
        singleLine = true,
        onValueChange = {
            trainingName = it
            viewModel.setTrainingName(it)
        }
    )
}

@Composable
fun ExerciseNameControls(
    currentName: String,
    onNameEntered: (value: String) -> Unit
) {
    var exerciseName = currentName
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        text = stringResource(id = R.string.name),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Box(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(percent = 10),
                color = Color(WhiteSmoke.toArgb()),
            )
            .height(36.dp)
            .padding(start = 12.dp, end = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            value = exerciseName,
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true,
            onValueChange = {
                exerciseName = it
                onNameEntered(it)
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableExerciseItem(
    vm: CreateTrainingViewModel,
    exercise: Exercise
) {
    val dismissState = DismissState(initialValue = DismissValue.Default, confirmStateChange = {
        if (it == DismissValue.DismissedToEnd) {
            vm.deleteExercise(exercise.listId!!)
        }
        true
    })

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        dismissThresholds = { FractionalThreshold(0.2f) },
        background = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.Default -> Color(WhiteSmoke.toArgb())
                    DismissValue.DismissedToEnd -> Color.Red
                    else -> {
                        Color(WhiteSmoke.toArgb())
                    }
                }
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(12.dp),
                Alignment.BottomStart
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.desc_delete_icon)
                )
            }
        },
        dismissContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(BananaMania.toArgb()))
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(exercise.name)
            }

        }
    )
}

private fun toDisplayableLength(exerciseDuration: Int): String {
    return if (exerciseDuration >= 60) {
        val mins = exerciseDuration / 60
        val rest = exerciseDuration.mod(60)
        "$mins min $rest sec"
    } else {
        "$exerciseDuration sec"
    }
}

private fun isTrainingBeingEdited(trainingId: Long): Boolean {
    return trainingId >= 0
}