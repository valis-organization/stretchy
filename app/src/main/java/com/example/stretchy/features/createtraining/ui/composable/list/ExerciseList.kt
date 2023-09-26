package com.example.stretchy.features.createtraining.ui.composable.list

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
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.widget.CreateExerciseWidget
import com.example.stretchy.features.createtraining.ui.data.BreakAfterExercise
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.features.createtraining.ui.list.DragDropLazyList
import com.example.stretchy.repository.Activity

@Composable
fun ExerciseList(
    exercises: List<Activity>,
    viewModel: CreateOrEditTrainingViewModel,
) {
    var editedExercise by remember { mutableStateOf(Exercise()) }
    var breakToEdit: BreakAfterExercise? by remember { mutableStateOf(BreakAfterExercise()) }
    var widgetVisible by remember { mutableStateOf(false) }

    DragDropLazyList(
        modifier = Modifier.heightIn(0.dp, 240.dp),
        items = getExercisesWithoutBreaks(exercises),
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
                    breakToEdit = getBreakAfterExercise(exercises = exercises, item.activityOrder)
                    editedExercise =
                        Exercise(
                            item.name,
                            item.activityOrder,
                            item.duration,
                            index,
                        )
                    widgetVisible = true
                }) {
            SwipeableExerciseItem(
                vm = viewModel,
                exercise = Exercise(
                    item.name,
                    item.activityOrder,
                    item.duration,
                    index
                ),
                getBreakAfterExercise(exercises = exercises, item.activityOrder)
            )
        }
    }
    CreateExerciseWidget(
        viewModel = viewModel,
        exerciseToEdit = editedExercise,
        breakToEdit = getBreakAfterExercise(exercises, editedExercise.activityOrder),
        widgetVisible = widgetVisible,
        onAddOrEditButtonClick = {
            widgetVisible = !widgetVisible
            editedExercise = Exercise()
        }
    )
}

private fun getExercisesWithoutBreaks(activities: List<Activity>): MutableList<Activity> {
    val new = mutableListOf<Activity>()
    activities.forEach {
        if (it.activityType != ActivityType.BREAK) {
            new.add(it)
        }
    }
    return new
}

private fun getBreakAfterExercise(
    exercises: List<Activity>,
    activityOrder: Int?
): BreakAfterExercise? {
    val exerciseBreak =
        exercises.find { it.activityOrder == activityOrder?.plus(1) && it.activityType == ActivityType.BREAK }
    return exerciseBreak?.let {
        BreakAfterExercise(
            it.duration
        )
    }
}

