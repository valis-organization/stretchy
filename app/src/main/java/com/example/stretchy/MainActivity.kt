package com.example.stretchy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stretchy.navigation.Navigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val exerciseListViewModel: ExerciseListViewModel = viewModel()
            Navigation(exerciseListViewModel)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        val exerciseListViewModel: ExerciseListViewModel = viewModel()
        Navigation(exerciseListViewModel)
    }

}