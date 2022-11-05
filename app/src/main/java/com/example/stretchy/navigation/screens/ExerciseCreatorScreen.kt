package com.example.stretchy.navigation.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Composable
fun ExerciseCreatorScreen() {
    val sliderMaxValue = 300
    val sliderSteps: Int = (sliderMaxValue / 60) - 1
    var sliderValue: Int by remember { mutableStateOf(0) }
    var exerciseDuration: Int by remember { mutableStateOf(0) }
    Column {
        ExerciseNameControls(onNameEntered = {})
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp),
            text = "Length: ${toDisplayableLength(exerciseDuration)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            modifier = Modifier.padding(16.dp),
            value = sliderValue.toFloat(),
            onValueChange = {
                sliderValue = it.toInt()
                exerciseDuration = sliderValue
            },
            valueRange = 0f..sliderMaxValue.toFloat(),
            steps = sliderSteps,
        )
        AddOrSubtractButtons { changeValue ->
            sliderValue += changeValue
            exerciseDuration = sliderValue
        }
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            onClick = {}
        ) { Text(text = "Add") }
    }
}

@Composable
fun AddOrSubtractButtons(onTextEntered: (value: Int) -> Unit) {
    Row {
        Button(
            // modifier = Modifier.widthIn(max = 5.dp),
            onClick = { onTextEntered(-10) })
        {
            Text(text = "-10")
        }
        Button(onClick = { onTextEntered(-5) })
        {
            Text(text = "-5")
        }
        Button(onClick = { onTextEntered(-1) })
        {
            Text(text = "-1")
        }
        Button(onClick = { onTextEntered(+1) })
        {
            Text(text = "+1")
        }
        Button(onClick = { onTextEntered(+5) })
        {
            Text(text = "+5")
        }
        Button(onClick = { onTextEntered(+10) })
        {
            Text(text = "+10")
        }
    }
}

@Composable
fun ExerciseNameControls(
    onNameEntered: (value: String) -> Unit
) {
    val exerciseName by remember { mutableStateOf("L-sit") }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp),
        text = "Name:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = exerciseName,
        onValueChange = {
            onNameEntered(it)
        },
    )
}

private fun toDisplayableLength(exerciseDuration: Int): String {
    return if (exerciseDuration >= 60) {
        val mins = exerciseDuration / 60
        val rest = exerciseDuration.mod(60)
        "$mins min $rest sec"
    } else {
        "$exerciseDuration sec"
    }
}

class Timer {
    private var _counterFlow: Flow<Int>? = null
    var flow: MutableStateFlow<Int> = MutableStateFlow(0)
    private var currentMs: Int = 0
    private var paused = true

    init {
        _counterFlow = (0..Int.MAX_VALUE)
            .asSequence()
            .asFlow()
            .onEach {
                delay(10)
                if (!paused) {
                    currentMs += 10
                    if (it.mod(100) == 0) {
                        flow.emit(currentMs / 1000)
                    }
                }
            }
        GlobalScope.launch{
            _counterFlow!!.collect()
        }
    }

    fun start() {
        paused = false
    }

    fun pause() {
        paused = true
    }


    companion object {
        private const val ONE_SECOND = 1_000L
    }
}