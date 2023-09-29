package com.example.stretchy.features.createtraining.ui.composable.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.widget.ExerciseAndBreakTabsWidget
import com.example.stretchy.features.createtraining.ui.composable.widget.toDisplayableLength
import com.example.stretchy.features.createtraining.ui.data.BreakAfterExercise
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.theme.BananaMania

@Composable
fun ExerciseListItem(
    vm: CreateOrEditTrainingViewModel,
    exercise: Exercise,
    breakAfterExercise: BreakAfterExercise?,
    trainingType: TrainingType,
    isAutoBreakClicked: Boolean,
    onEditClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    if (!isExpanded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(72.dp)
                .padding(8.dp)
                .background(Color(BananaMania.toArgb()))
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(64.dp)
                        .background(Color.Gray)
                        .clickable { isExpanded = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (breakAfterExercise != null) {
                        Text(toDisplayableLength(breakAfterExercise.duration!!))
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_no_break),
                            contentDescription = ""
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .clickable {
                            isExpanded = true
                        },
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
    } else {
        val isTimelessExercise by remember { mutableStateOf(exercise.duration == 0) }
        val layoutHeight = if (isTimelessExercise) 200.dp else 300.dp

        Box(Modifier.height(layoutHeight)) {
            ExerciseAndBreakTabsWidget(
                viewModel = vm,
                exerciseToEdit = exercise,
                breakToEdit = breakAfterExercise,
                onAddOrEditButtonClick = {
                    isExpanded = false
                    onEditClick()
                },
                trainingType = trainingType,
                isAutoBreakClicked = isAutoBreakClicked
            )
        }
    }
}