package com.example.stretchy

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.navigation.screens.ExercisePlansScreen
import com.example.stretchy.navigation.screens.ExerciseCreatorScreen
import com.example.stretchy.features.training.ui.ExerciseScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ExercisePlansScreen.route) {
        composable(route = Screen.ExercisePlansScreen.route) {
            ExercisePlansScreen(navController = navController)
        }
        composable(route = Screen.ExerciseCreatorScreen.route) {
            ExerciseCreatorScreen()
        }
        composable(route = Screen.ExerciseScreen.route) {
            ExerciseScreen()
        }
    }
}