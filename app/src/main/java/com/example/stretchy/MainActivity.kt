package com.example.stretchy

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.stretchy.navigation.Navigation
import com.example.stretchy.navigation.screens.Timer
import com.example.stretchy.ui.theme.StretchyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            StretchyTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(exercisesList)
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        StretchyTheme(darkTheme = false) {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Navigation(exercisesList = exercisesList)
            }
        }
    }
}