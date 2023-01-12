package com.example.stretchy

sealed class Screen(val route: String) {
    object TrainingListScreen : Screen("trainingListScreen")
    object ExerciseCreatorScreen : Screen("exerciseCreatorScreen?id={id}")
}
