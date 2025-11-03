package com.example.stretchy.features.executetraining.ui.composable.pager.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.TextSpacer


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimelessExerciseScreenn(
    exerciseName: String,
    nextExerciseName: String?,
    viewModel: ExecuteTrainingViewModel
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextSpacer(fontSize = 16.sp)
        AnimatedContent(
            targetState = exerciseName
        ) {
            Box(
                Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .weight(1f, fill = false)
            ) {
                Text(text = it, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(130.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_no_break),
            contentDescription = "",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))
        if (!nextExerciseName.isNullOrBlank()) {
            Text(
                text = "Swipe for next exercise",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
        } else {
            Button(onClick = { viewModel.endTraining() }) {
                Text(
                    text = "End training",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedContent(
            targetState = nextExerciseName
        ) {
            Box(
                Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .weight(1f, fill = false)
            ) {
                Text(text = it ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Mock ViewModel for previews
private fun createMockExecuteTrainingViewModel(): ExecuteTrainingViewModel {
    val mockRepository = object : com.example.stretchy.repository.Repository {
        override suspend fun getTrainingsWithActivities() = emptyList<com.example.stretchy.repository.TrainingWithActivity>()
        override suspend fun getTrainingWithActivitiesById(id: Long) = com.example.stretchy.repository.TrainingWithActivity(
            name = "Preview Training",
            trainingType = com.example.stretchy.database.data.TrainingType.STRETCH,
            finished = false,
            activities = emptyList()
        ).apply { this.id = 1L }
        override suspend fun addTrainingWithActivities(training: com.example.stretchy.repository.TrainingWithActivity) {}
        override suspend fun editTrainingWithActivities(trainingId: Long, editedTraining: com.example.stretchy.repository.TrainingWithActivity) {}
        override suspend fun deleteTrainingById(trainingId: Long) {}
    }

    // Mock SavedStateHandle with trainingId
    val mockSavedStateHandle = androidx.lifecycle.SavedStateHandle().apply {
        set("id", "1")
    }

    // Use the new Hilt-based constructor that takes Repository and SavedStateHandle
    return ExecuteTrainingViewModel(mockRepository, mockSavedStateHandle)
}

@Preview(name = "Timeless Exercise - With next exercise", showBackground = true)
@Composable
private fun TimelessExerciseWithNextPreview() {
    TimelessExerciseScreenn(
        exerciseName = "Plank Hold",
        nextExerciseName = "Mountain Climbers",
        viewModel = createMockExecuteTrainingViewModel()
    )
}

@Preview(name = "Timeless Exercise - Final exercise", showBackground = true)
@Composable
private fun TimelessExerciseFinalPreview() {
    TimelessExerciseScreenn(
        exerciseName = "Final Relaxation",
        nextExerciseName = null,
        viewModel = createMockExecuteTrainingViewModel()
    )
}

@Preview(name = "Timeless Exercise - Long name", showBackground = true)
@Composable
private fun TimelessExerciseLongNamePreview() {
    TimelessExerciseScreenn(
        exerciseName = "Advanced Deep Core Stabilization with Breath Control",
        nextExerciseName = "Cool Down Stretch",
        viewModel = createMockExecuteTrainingViewModel()
    )
}

