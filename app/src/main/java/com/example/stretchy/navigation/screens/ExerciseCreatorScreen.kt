package com.example.stretchy.navigation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExerciseCreatorScreen(onTextEntered: (value: String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp),
                text = "Name:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            var sliderPos: Int by remember { mutableStateOf(0) }
            var exerciseLength by remember { mutableStateOf("") }
            var exerciseName by remember { mutableStateOf("Hello") }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = exerciseName,
                onValueChange = {
                    exerciseName = it
                    onTextEntered(it)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            var displayableLength: String by remember {
                mutableStateOf("")
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp),
                text = toDisplayableLength(displayableLength),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Text(text = "Stretches", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = exerciseLength,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { input ->
                    if (input.isNotBlank()) {
                        if (input.length <= 3
                            && input.toInt() <= 300
                            && !input.startsWith("0")
                        ) {
                            sliderPos = input.toInt()
                            exerciseLength = input
                            displayableLength = input
                        }
                    }
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = sliderPos.toFloat(),
                onValueChange = {
                    sliderPos = it.toInt()
                    displayableLength = sliderPos.toString()
                },
                valueRange = 0f..300f,
                onValueChangeFinished = {
                    // launch some business logic update with the state you hold
                    // viewModel.updateSelectedSliderValue(sliderPosition)
                },
                steps = 5,
            )

        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onClick = {

            }
        ) {
            Text(
                text = "Add"
            )
        }
    }
}

private fun toDisplayableLength(displayableLength: String): String {
    val timeVal = if (displayableLength.isBlank()) {
        "0 sec"
    } else {
        val lInt = displayableLength.toInt()
        if (lInt >= 60) {
            val mins = lInt / 60
            val rest = lInt.mod(60)
            "$mins min $rest sec"
        } else {
            "$displayableLength sec"
        }
    }
    return "Length: $timeVal"
}