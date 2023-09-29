package com.example.stretchy

sealed class Screen(val route: String) {
    object StretchingListScreen : Screen("stretchingListScreen")
    object ExerciseCreatorScreen : Screen("exerciseCreatorScreen?id={id}&trainingType={trainingType}")
    object TrainingListScreen : Screen("trainingListScreen")
}
