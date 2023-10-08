package com.example.stretchy.features.createtraining.ui.composable.list.listitem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.widget.ExerciseAndBreakTabsWidget
import com.example.stretchy.features.createtraining.ui.composable.widget.OnListExerciseHandler

@Composable
fun ExpandedItem(
    exerciseWithBreaks: ExercisesWithBreaks,
    onCollapse: () -> Unit,
    onListExerciseHandler: OnListExerciseHandler
) {
    var layoutHeight by remember { mutableStateOf(300.dp) }

    Box(androidx.compose.ui.Modifier.height(layoutHeight)) {
        ExerciseAndBreakTabsWidget(
            exerciseWithBreakToEdit = exerciseWithBreaks,
            onAddOrEditButtonClick = {
                onCollapse()
            },
            onTabSizeChange = {
                layoutHeight = when (it) {
                    ActivityType.STRETCH -> 300.dp
                    ActivityType.EXERCISE -> 300.dp
                    ActivityType.TIMELESS_EXERCISE -> 230.dp
                    ActivityType.BREAK -> 230.dp
                }
            }, onListExerciseHandler = onListExerciseHandler
        )
    }
}