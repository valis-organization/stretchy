package com.example.stretchy.features.executetraining.ui.composable.pager.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.features.executetraining.ui.composable.TextSpacer
import com.example.stretchy.features.executetraining.ui.composable.timer.AnalogTimerClock


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExerciseVieww(
    exerciseName: String,
    nextExerciseName: String?,
    currentTime: Float,
    totalTime: Int
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
        Spacer(modifier = Modifier.height(100.dp))
        AnalogTimerClock(
            timeRemaining = currentTime,
            totalTime = totalTime.toFloat() * 1000,
            modifier = Modifier.size(300.dp),
            isBreak = false
        )
        Spacer(modifier = Modifier.height(36.dp))
        if (!nextExerciseName.isNullOrBlank()) {
            Text(
                text = stringResource(id = R.string.nxt_exercise),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
        } else {
            TextSpacer(fontSize = 16.sp)
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

@Preview(name = "Exercise - With next exercise", showBackground = true)
@Composable
private fun ExerciseWithNextPreview() {
    ExerciseVieww(
        exerciseName = "Push Ups",
        nextExerciseName = "Mountain Climbers",
        currentTime = 15000f,
        totalTime = 30
    )
}

@Preview(name = "Exercise - Without next exercise", showBackground = true)
@Composable
private fun ExerciseWithoutNextPreview() {
    ExerciseVieww(
        exerciseName = "Final Plank Hold",
        nextExerciseName = null,
        currentTime = 45000f,
        totalTime = 60
    )
}

@Preview(name = "Exercise - Long name", showBackground = true)
@Composable
private fun ExerciseLongNamePreview() {
    ExerciseVieww(
        exerciseName = "Advanced Mountain Climbers with Side Rotation",
        nextExerciseName = "Cool Down Stretch",
        currentTime = 10000f,
        totalTime = 45
    )
}

