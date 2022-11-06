package com.example.stretchy.navigation.screens

sealed class Screen(val route: String) {
    object ExercisePlansScreen : Screen("exercisePlansScreen")
    object ExerciseCreatorScreen : Screen("exerciseCreatorScreen")
    object ExerciseScreen : Screen("exerciseScreen")
}
