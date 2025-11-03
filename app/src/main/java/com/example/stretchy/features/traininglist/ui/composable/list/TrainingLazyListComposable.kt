package com.example.stretchy.features.traininglist.ui.composable.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.Screen

import com.example.stretchy.features.traininglist.ui.data.Training

@Composable
fun TrainingLazyListVieww(
    trainingList: List<Training>,
    navController: NavController,
    onDeleteTraining: (Training) -> Unit,
    onCopyTraining: (Training) -> Unit
) {
    LazyColumn {
        items(trainingList) { training ->
            Box(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.ExecuteTrainingScreen.createRoute(training.id))
                },
            ) {
                TrainingListItemVieww(
                    training = training,
                    navController = navController,
                    onDeleteTraining = onDeleteTraining,
                    onCopyTraining = onCopyTraining
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(name = "Training List - Few items", showBackground = true)
@Composable
private fun TrainingListFewItemsPreview() {
    val sampleTrainings = listOf(
        Training(
            id = "1",
            name = "Morning Stretch",
            numberOfExercises = 5,
            timeInSeconds = 300,
            type = Training.Type.STRETCH
        ),
        Training(
            id = "2",
            name = "Evening Workout",
            numberOfExercises = 8,
            timeInSeconds = 480,
            type = Training.Type.BODY_WEIGHT
        )
    )

    TrainingLazyListVieww(
        trainingList = sampleTrainings,
        navController = rememberNavController(),
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

@Preview(name = "Training List - Many items", showBackground = true)
@Composable
private fun TrainingListManyItemsPreview() {
    val sampleTrainings = listOf(
        Training("1", "Morning Stretch", 5, 300, Training.Type.STRETCH),
        Training("2", "Evening Workout", 8, 480, Training.Type.BODY_WEIGHT),
        Training("3", "Quick HIIT", 12, 900, Training.Type.BODY_WEIGHT),
        Training("4", "Yoga Flow", 7, 1200, Training.Type.STRETCH),
        Training("5", "Core Blast", 10, 600, Training.Type.BODY_WEIGHT)
    )

    TrainingLazyListVieww(
        trainingList = sampleTrainings,
        navController = rememberNavController(),
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

