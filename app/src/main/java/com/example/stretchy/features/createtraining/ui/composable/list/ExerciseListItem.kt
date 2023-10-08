package com.example.stretchy.features.createtraining.ui.composable.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.composable.widget.ExerciseAndBreakTabsWidget
import com.example.stretchy.features.createtraining.ui.composable.widget.OnListExerciseHandler
import com.example.stretchy.features.createtraining.ui.composable.widget.toDisplayableLength
import com.example.stretchy.theme.BananaMania

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
                        .clickable {
                            onExpand()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (exerciseWithBreaks.nextBreakDuration != 0 && exerciseWithBreaks.nextBreakDuration != null) {
                        Text(toDisplayableLength(exerciseWithBreaks.nextBreakDuration!!))
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
                            onExpand()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${position + 1}")

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = exerciseWithBreaks.exercise.name,
                            Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    } else {
        var layoutHeight by remember {
            mutableStateOf(300.dp)
        }

        Box(Modifier.height(layoutHeight)) {
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
}