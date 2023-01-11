package com.example.stretchy.features.createtraining.ui.composable

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity
import com.example.stretchy.theme.BananaMania
import com.example.stretchy.theme.WhiteSmoke
import kotlin.math.roundToInt

@Composable
fun CreateTrainingComposable(
    navController: NavController,
    viewModel: CreateTrainingViewModel
) {
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
            TrainingName(viewModel)
            Spacer(modifier = Modifier.height(24.dp))
            when (val state = viewModel.uiState.collectAsState().value) {
                is CreateTrainingUiState.Success -> {
                    ExerciseList(exercises = state.training, viewModel = viewModel)
                }
                is CreateTrainingUiState.Editing -> {
                    trainingId = state.trainingId
                    ExerciseList(exercises = state.activities, viewModel = viewModel)
                }
                else -> {
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
                    Text("Edit")
                } else {
                    Text(stringResource(id = R.string.create_training))
                }
            }
        }
    }
}

@Composable
private fun ExerciseList(exercises: List<Activity>, viewModel: CreateTrainingViewModel) {
    LazyColumn(
        modifier = Modifier.heightIn(0.dp, 240.dp),
        verticalArrangement = Arrangement.Top
    ) {
        itemsIndexed(exercises) { listPosition, exercise ->
            SwipeableExerciseItem(
                vm = viewModel,
                item = Exercise(exercise.name, exercise.duration),
                listId = listPosition
            )
        }
    }
    CreateExerciseWidget(viewModel = viewModel)
}

@Composable
fun CreateExerciseWidget(viewModel: CreateTrainingViewModel) {
    val minExerciseDuration = 10
    var visible by remember { mutableStateOf(false) }
    val sliderMaxValue = 300
    var sliderValue: Int by remember { mutableStateOf(minExerciseDuration) }
    var exerciseDuration: Int by remember { mutableStateOf(minExerciseDuration) }
    var exerciseName = ""
    val context = LocalContext.current
    AnimatedVisibility(visible = !visible) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color(BananaMania.toArgb()))
                .clickable {
                    visible = !visible
                }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Column(
            Modifier
                .padding(start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.LightGray)
                .padding(start = 12.dp, end = 12.dp)
        ) {
            ExerciseNameControls(onNameEntered = { exerciseName = it })
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = "Duration: ${toDisplayableLength(exerciseDuration)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = sliderValue.toFloat(),
                onValueChange = {
                    sliderValue = it.toInt()
                    exerciseDuration = sliderValue
                },
                valueRange = 10f..sliderMaxValue.toFloat(),
            )
            AddOrSubtractButtons { changeValue ->
                if (sliderValue + changeValue in 10..300) {
                    sliderValue += changeValue
                    exerciseDuration = sliderValue
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 16.dp),
                onClick = {
                    if (exerciseName.isNotEmpty() && sliderValue != 0) {
                        visible = !visible
                        viewModel.addActivity(
                            Activity(
                                exerciseName,
                                exerciseDuration,
                                ActivityType.STRETCH
                            )
                        )
                        Toast.makeText(context, "Exercise added", Toast.LENGTH_LONG).show()
                        sliderValue = minExerciseDuration
                        exerciseDuration = minExerciseDuration
                    } else {
                        Toast.makeText(
                            context,
                            "You need to specify exercise name!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) { Text(text = "Add Exercise") }
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
fun TrainingName(viewModel: CreateTrainingViewModel) {
    var trainingName by remember { mutableStateOf("") }

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
    onNameEntered: (value: String) -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
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
            onValueChange = {
                exerciseName = it
                onNameEntered(it)
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableExerciseItem(vm: CreateTrainingViewModel, item: Exercise, listId: Int) {
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { 100.dp.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(WhiteSmoke.toArgb()))
    ) {
        SwipeActions(vm, listId)
        Box(modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
            .background(Color(BananaMania.toArgb()))
            .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(item.name)
        }
    }
}

@Composable
fun SwipeActions(viewModel: CreateTrainingViewModel, exerciseId: Int) {
    Row() {
        IconButton(
            modifier = Modifier.padding(top = 2.dp),
            onClick = { viewModel.deleteExercise(exerciseId) }) {
            Icon(Icons.Filled.Delete, "Delete exercise")
        }
        IconButton(modifier = Modifier.padding(top = 2.dp), onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Edit, "asd")
        }
    }
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