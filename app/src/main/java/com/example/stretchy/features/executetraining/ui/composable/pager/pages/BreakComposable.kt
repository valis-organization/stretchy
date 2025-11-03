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
import com.example.stretchy.features.executetraining.ui.composable.timer.TimerVieww


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BreakVieww(
    nextExerciseName: String,
    currentTime: Float,
    totalTime: Int
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${stringResource(id = R.string.prepare_next_exercise)} ",
            fontSize = 16.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Bold
        )
        AnimatedContent(
            targetState = nextExerciseName
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
        TimerVieww(
            isBreak = true,
            totalSeconds = totalTime.toFloat() * 1000,
            modifier = Modifier.size(300.dp),
            currentSeconds = currentTime
        )
        Spacer(modifier = Modifier.height(44.dp))
        TextSpacer(fontSize = 40.sp)
    }
}

@Preview(name = "Break - Short exercise name", showBackground = true)
@Composable
private fun BreakShortNamePreview() {
    BreakVieww(
        nextExerciseName = "Push Ups",
        currentTime = 5000f,
        totalTime = 10
    )
}

@Preview(name = "Break - Long exercise name", showBackground = true)
@Composable
private fun BreakLongNamePreview() {
    BreakVieww(
        nextExerciseName = "Advanced Mountain Climbers with Rotation",
        currentTime = 3000f,
        totalTime = 15
    )
}

@Preview(name = "Break - Almost finished", showBackground = true)
@Composable
private fun BreakAlmostFinishedPreview() {
    BreakVieww(
        nextExerciseName = "Plank Hold",
        currentTime = 1000f,
        totalTime = 10
    )
}

