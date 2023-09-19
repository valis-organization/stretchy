package com.example.stretchy.features.createtraining.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
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
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.theme.BananaMania

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
                editedExercise.listId!!, //TODO
                exerciseDuration,
                viewModel,
                editedExercise,
                onAddOrEditButtonClick
            )
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