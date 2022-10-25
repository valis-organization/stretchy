package com.example.stretchy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.stretchy.navigation.Navigation

class MainActivity : ComponentActivity() {
    private val exercise1 = ExerciseInfo(
        itemName = "Recommendation name",
        numberOfExercises = 11,
        timeInSeconds = 397
    )

    private val exercise2 = ExerciseInfo(
        itemName = "Exercise name",
        numberOfExercises = 7,
        timeInSeconds = 203
    )

    private val exercisesList = mutableListOf(exercise1, exercise2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigation(exercisesList)
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Navigation(exercisesList = exercisesList)
    }

}