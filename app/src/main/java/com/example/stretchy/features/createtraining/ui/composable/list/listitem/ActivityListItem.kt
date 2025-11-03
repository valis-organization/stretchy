package com.example.stretchy.features.createtraining.ui.composable.list.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.widget.AddExerciseButtonHandler

@Composable
fun ActivityListItem(
    exerciseWithBreaks: ExercisesWithBreaks,
    position: Int,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    addExerciseButtonHandler: AddExerciseButtonHandler
) {

    if (!isExpanded) {
        CollapsedActivityItem(exerciseWithBreaks = exerciseWithBreaks, position = position) {
            onExpand()
        }
    } else {
        ExpandedItem(
            exerciseWithBreaks,
            onCollapse,
            addExerciseButtonHandler
        )
    }
}

@Preview(name = "Activity List Item - Collapsed", showBackground = true)
@Composable
private fun ActivityListItemCollapsedPreview() {
    val sampleExercise = com.example.stretchy.features.createtraining.ui.data.Exercise(
        id = 1,
        name = "Push Ups",
        duration = 30
    )
    val exerciseWithBreaks = ExercisesWithBreaks(
        listId = 0,
        exercise = sampleExercise,
        nextBreakDuration = 10,
        isExpanded = false
    )

    val mockHandler = object : AddExerciseButtonHandler {
        override fun hideButton() {}
        override fun showButton() {}
    }

    ActivityListItem(
        exerciseWithBreaks = exerciseWithBreaks,
        position = 0,
        isExpanded = false,
        onExpand = {},
        onCollapse = {},
        addExerciseButtonHandler = mockHandler
    )
}
