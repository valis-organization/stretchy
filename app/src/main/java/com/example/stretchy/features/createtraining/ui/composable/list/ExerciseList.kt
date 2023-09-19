package com.example.stretchy.features.createtraining.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.features.createtraining.ui.list.DragDropLazyList
import com.example.stretchy.repository.Activity

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
