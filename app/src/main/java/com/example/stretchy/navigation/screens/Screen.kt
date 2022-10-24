package com.example.stretchy.navigation.screens

sealed class Screen(val route: String) {
    object ExercisesListScreen : Screen("exercisesListScreen")
    object ExerciseCreatorScreen : Screen("exerciseCreatorScreen")
    object ExerciseScreen : Screen("exerciseScreen")
}
