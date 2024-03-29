package com.example.stretchy.features.createtraining.ui.composable

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
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
                    isTrainingBeingEdited = state.editingTraining
                    Spacer(modifier = Modifier.height(24.dp))
                    ExerciseList(exercises = state.activities, viewModel = viewModel)
                }
                is CreateTrainingUiState.Error -> {
                    HandleError(state = state, context = context)
                }
                is CreateTrainingUiState.Done -> {

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
            CreateOrEditTrainingButton(viewModel, isTrainingBeingEdited, navController, trainingId)
        }
    }
}

@Composable
fun CreateOrEditTrainingButton(
    viewModel: CreateOrEditTrainingViewModel,
    isTrainingBeingEdited: Boolean,
    navController: NavController,
    trainingId: Long?
) {
    val buttonCanBeClicked = viewModel.uiState.collectAsState().value.saveButtonCanBeClicked
    val buttonColor =
        if (buttonCanBeClicked) ButtonDefaults.buttonColors(backgroundColor = Color(BananaMania.toArgb())) else ButtonDefaults.buttonColors(
            backgroundColor = Color.Gray
        )
    val textColors = if (buttonCanBeClicked) Color.Black else Color.DarkGray
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {
            if (buttonCanBeClicked) {
                if (isTrainingBeingEdited) {
                    viewModel.editTraining(trainingId = trainingId!!)
                } else {
                    viewModel.createTraining()
                }
                if (viewModel.uiState.value is CreateTrainingUiState.Done) {
                    navController.navigate(Screen.TrainingListScreen.route)
                }
            }
        },
        colors = buttonColor
    ) {
        if (isTrainingBeingEdited) {
            if (viewModel.uiState.collectAsState().value.isTrainingChanged) {
                Text(
                    stringResource(id = R.string.save_changes),
                    fontWeight = FontWeight.Bold,
                    color = textColors
                )
            } else {
                Text(
                    stringResource(id = R.string.close_editing),
                    fontWeight = FontWeight.Bold,
                    color = textColors
                )
            }

        } else {
            Text(
                stringResource(id = R.string.create_training),
                fontWeight = FontWeight.Bold,
                color = textColors
            )
        }
    }
}

@Composable
fun ExerciseList(exercises: List<Activity>, viewModel: CreateOrEditTrainingViewModel) {
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
        onAddOrEditButtonClick = {
            widgetVisible = !widgetVisible
            editedExercise = Exercise()
        })
}

@Composable
fun CreateExerciseWidget(
    viewModel: CreateOrEditTrainingViewModel,
    editedExercise: Exercise,
    widgetVisible: Boolean,
    onAddOrEditButtonClick: () -> Unit
) {
    val sliderMinValue = 10
    val sliderMaxValue = 300
    var exerciseDuration: Int by remember { mutableStateOf(sliderMinValue) }
    var exerciseName: String by remember { mutableStateOf("") }

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
                    onAddOrEditButtonClick()
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
            ExerciseNameControls(
                currentName = exerciseName,
                onNameEntered = { exerciseName = it })
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = stringResource(R.string.duration) + " ${
                    toDisplayableLength(
                        exerciseDuration
                    )
                }",
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
            AddOrEditExerciseButton(
                exerciseName,
                exerciseDuration,
                viewModel,
                editedExercise,
                onAddOrEditButtonClick
            )
        }
    }
}

@Composable
fun AddOrEditExerciseButton(
    exerciseName: String,
    exerciseDuration: Int,
    viewModel: CreateOrEditTrainingViewModel,
    editedExercise: Exercise,
    onAddOrEditButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val exerciseIsBeingEdited: Boolean = editedExercise.name != ""
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 16.dp),
        onClick = {
            if (exerciseName.isNotEmpty() && exerciseDuration != 0) {
                onAddOrEditButtonClick()
                if (exerciseIsBeingEdited) {
                    if (exerciseName != editedExercise.name || exerciseDuration != editedExercise.duration) {
                        viewModel.editActivity(
                            Activity(
                                exerciseName,
                                exerciseDuration,
                                ActivityType.STRETCH
                            ), editedExercise.listId!!
                        )
                        Toast.makeText(context, R.string.exercise_edited, Toast.LENGTH_LONG)
                            .show()
                    }
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
            if (exerciseName == editedExercise.name && exerciseDuration == editedExercise.duration) {
                Text(text = stringResource(id = R.string.close_item))
            } else {
                Text(text = stringResource(id = R.string.save_changes))
            }
        } else {
            Text(text = stringResource(id = R.string.add_exercise))
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
fun TrainingName(viewModel: CreateOrEditTrainingViewModel, initialTrainingName: String) {
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
    vm: CreateOrEditTrainingViewModel,
    exercise: Exercise
) {
    val dismissState = DismissState(initialValue = DismissValue.Default, confirmStateChange = {
        if (it == DismissValue.DismissedToEnd) {
            vm.removeLocalActivity(exercise.listId!!)
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
                    .clip(RoundedCornerShape(10.dp))
                    .weight(1f, fill = false),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${exercise.listId!!.plus(1)}")
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = exercise.name, Modifier.padding(start = 16.dp))
                    }
                }
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

@Composable
private fun HandleError(state: CreateTrainingUiState.Error, context: Context) {
    Log.e("Error", state.reason.toString())
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