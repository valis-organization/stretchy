package com.example.stretchy.features.traininglist.ui.composable.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.stretchy.R
import com.example.stretchy.common.convertSecondsToMinutes

import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.theme.WhiteSmoke

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrainingListItemVieww(
    training: Training,
    onEditTraining: (String, String) -> Unit,
    onDeleteTraining: (Training) -> Unit,
    onCopyTraining: (Training) -> Unit
) {

    DismissState(initialValue = DismissValue.Default, confirmStateChange = {
        if (it == DismissValue.DismissedToEnd) {
            onDeleteTraining(training)
        }
        true
    }).also {

        SwipeToDismiss(
            state = it,
            directions = setOf(DismissDirection.StartToEnd),
            dismissThresholds = { FractionalThreshold(0.2f) },
            background = {
                DismissBackgroundLayout(dismissState = it)
            },
            dismissContent = {
                Box(
                    Modifier
                        .background(color = Color.White)
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 20.dp),
                    Alignment.Center
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = training.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Row {
                                EditIconButton(
                                    onEditTraining = onEditTraining,
                                    trainingId = training.id,
                                    trainingType = training.type
                                )
                                Spacer(Modifier.width(16.dp))
                                CopyIconButton(onCopyTraining = onCopyTraining, training = training)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.LightGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        ExerciseDetailsRow(training = training)
                    }
                }
            }
        )
    }
}

@Composable
private fun EditIconButton(
    onEditTraining: (String, String) -> Unit,
    trainingId: String,
    trainingType: Training.Type
) {
    IconButton(
        onClick = {
            val routeTrainingType = if (trainingType == Training.Type.BODY_WEIGHT) "BODYWEIGHT" else "STRETCH"
            onEditTraining(trainingId, routeTrainingType)
        },
        Modifier
            .size(20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = stringResource(id = R.string.desc_edit_icon),
        )
    }
}

@Composable
private fun CopyIconButton(onCopyTraining: (Training) -> Unit, training: Training) {
    IconButton(
        onClick = {
            onCopyTraining(training)
        },
        Modifier
            .size(20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_copy),
            contentDescription = stringResource(id = R.string.desc_copy_icon),
        )
    }
}

@Composable
private fun ExerciseDetailsRow(training: Training) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(R.string.exercises),
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${training.numberOfExercises}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(100.dp))
        Column {
            Text(
                text = stringResource(R.string.estimated_time),
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = convertSecondsToMinutes(training.timeInSeconds.toLong()),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DismissBackgroundLayout(dismissState: DismissState) {
    val color by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            DismissValue.Default -> Color(WhiteSmoke.toArgb())
            DismissValue.DismissedToEnd -> Color.Red
            else -> {
                Color(WhiteSmoke.toArgb())
            }
        }
    )
    val icon = Icons.Default.Delete
    Box(
        Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp),
        Alignment.CenterStart
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = R.string.desc_delete_icon),
            modifier = Modifier.size(44.dp)
        )
    }
}

@Preview(name = "Training Item - Stretch workout", showBackground = true)
@Composable
private fun TrainingItemStretchPreview() {
    TrainingListItemVieww(
        training = Training(
            id = "1",
            name = "Morning Stretch Routine",
            numberOfExercises = 8,
            timeInSeconds = 600,
            type = Training.Type.STRETCH
        ),
        onEditTraining = { _, _ -> },
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

@Preview(name = "Training Item - Body weight workout", showBackground = true)
@Composable
private fun TrainingItemBodyWeightPreview() {
    TrainingListItemVieww(
        training = Training(
            id = "2",
            name = "HIIT Circuit Training",
            numberOfExercises = 12,
            timeInSeconds = 1800,
            type = Training.Type.BODY_WEIGHT
        ),
        onEditTraining = { _, _ -> },
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

@Preview(name = "Training Item - Long name", showBackground = true)
@Composable
private fun TrainingItemLongNamePreview() {
    TrainingListItemVieww(
        training = Training(
            id = "3",
            name = "Advanced Full Body Strength and Flexibility Training Session",
            numberOfExercises = 15,
            timeInSeconds = 2700,
            type = Training.Type.BODY_WEIGHT
        ),
        onEditTraining = { _, _ -> },
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

