package com.example.stretchy

sealed class Screen(val route: String) {
    object ExercisePlansScreen : Screen("exercisePlansScreen")
    object ExerciseCreatorScreen : Screen("exerciseCreatorScreen")
}
