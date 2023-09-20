package com.example.stretchy.features.createtraining.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.buttons.AddOrEditExerciseButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.AddOrSubtractButtons
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.theme.*

@Composable
fun CreateExerciseWidget(
    viewModel: CreateOrEditTrainingViewModel,
    editedExercise: Exercise,
    widgetVisible: Boolean,
    onAddOrEditButtonClick: () -> Unit
) {
    AnimatedVisibility(visible = !widgetVisible) {
        AddExerciseWidget {
            onAddOrEditButtonClick()
        }
    }
    AnimatedVisibility(
        visible = widgetVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        CreationExerciseWidget(viewModel, editedExercise, onAddOrEditButtonClick)
    }
}

@Composable
private fun AddExerciseWidget(onAddOrEditButtonClick: () -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)

    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
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
}

@Composable
private fun CreationExerciseWidget(
    viewModel: CreateOrEditTrainingViewModel,
    editedExercise: Exercise,
    onAddOrEditButtonClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs =
        listOf(stringResource(id = R.string.exercise), stringResource(id = R.string.exercise_break))

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)

    ) {
        Column(
            Modifier
                .background(color = Color(BananaMania.toArgb()))
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = Color(Purple500.toArgb()),
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(text = title, color = Color.Black) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                        },
                        modifier = Modifier.background(Color(Caramel.toArgb()))
                    )
                }
            }

            Column(
                Modifier
                    .padding(start = 12.dp, end = 12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> CreationOfExercise(
                        viewModel = viewModel,
                        editedExercise = editedExercise
                    ) {
                        onAddOrEditButtonClick()
                    }
                    1 -> CreationOfBreak(viewModel = viewModel, editedBreak = editedExercise) {
                        onAddOrEditButtonClick()
                    }
                }
            }
        }
    }
}

@Composable
private fun CreationOfExercise(
    viewModel: CreateOrEditTrainingViewModel,
    editedExercise: Exercise,
    onAddOrEditButtonClick: () -> Unit
) {

    val sliderMaxValue = 300
    var exerciseDuration: Int by remember { mutableStateOf(editedExercise.duration) }
    var exerciseName: String by remember { mutableStateOf(editedExercise.name) }

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
        modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
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

@Composable
private fun CreationOfBreak(
    viewModel: CreateOrEditTrainingViewModel,
    editedBreak: Exercise,
    onAddOrEditButtonClick: () -> Unit
) {

    val sliderMinValue = 10
    val sliderMaxValue = 300
    var breakDuration: Int by remember { mutableStateOf(sliderMinValue) }

    breakDuration = editedBreak.duration

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        text = stringResource(R.string.duration) + " ${
            toDisplayableLength(
                breakDuration
            )
        }",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Slider(
        value = breakDuration.toFloat(),
        onValueChange = {
            breakDuration = it.toInt()
        },
        valueRange = 10f..sliderMaxValue.toFloat(),
    )
    AddOrSubtractButtons { changeValue ->
        if (breakDuration + changeValue in 10..300) {
            breakDuration += changeValue
        }
    }
    AddOrEditExerciseButton(
        "",
        breakDuration,
        viewModel,
        editedBreak,
        onAddOrEditButtonClick
    )
}

fun toDisplayableLength(exerciseDuration: Int): String {
    return if (exerciseDuration >= 60) {
        val mins = exerciseDuration / 60
        val rest = exerciseDuration.mod(60)
        "$mins min $rest sec"
    } else {
        "$exerciseDuration sec"
    }
}

/*
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
*/
