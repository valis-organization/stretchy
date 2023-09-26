package com.example.stretchy.features.createtraining.ui.composable.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.widget.toDisplayableLength
import com.example.stretchy.features.createtraining.ui.data.BreakAfterExercise
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.theme.BananaMania
import com.example.stretchy.theme.WhiteSmoke

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableExerciseItem(
    vm: CreateOrEditTrainingViewModel,
    exercise: Exercise,
    breakAfterExercise: BreakAfterExercise?
) {
    val dismissState = DismissState(initialValue = DismissValue.Default, confirmStateChange = {
        if (it == DismissValue.DismissedToEnd) {
            vm.removeLocalActivity(exercise.activityOrder!!)
        }
        true
    })

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd),
        dismissThresholds = { FractionalThreshold(0.2f) },
        background = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.Default -> Color(WhiteSmoke.toArgb())
                    DismissValue.DismissedToEnd -> Color.Red
                    else -> {
                        Color(WhiteSmoke.toArgb())
                    }
                }
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(12.dp),
                Alignment.BottomStart
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.desc_delete_icon)
                )
            }
        },
        dismissContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(BananaMania.toArgb()))
                    .clip(RoundedCornerShape(10.dp))
                    .weight(1f, fill = false),
                contentAlignment = Alignment.Center
            ) {

                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(64.dp)
                            .background(Color.Gray),
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
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${exercise.listId!!.plus(1)}")

                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = exercise.name, Modifier.padding(start = 16.dp))
                    }
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(40.dp)
                            .background(Color.Gray),
                        contentAlignment = Alignment.CenterEnd
                    ) {

                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(40.dp)
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }
    )
}