package com.example.stretchy.features.createtraining.ui.composable.list.listitem

import androidx.compose.runtime.Composable
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.widget.OnListExerciseHandler

@Composable
fun ExerciseListItem(
    exerciseWithBreaks: ExercisesWithBreaks,
    position: Int,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    onListExerciseHandler: OnListExerciseHandler
) {

    if (!isExpanded) {
        CollapsedItem(exerciseWithBreaks = exerciseWithBreaks, position = position) {
            onExpand()
        }
    } else {
        ExpandedItem(exerciseWithBreaks, onCollapse, onListExerciseHandler)
    }
}