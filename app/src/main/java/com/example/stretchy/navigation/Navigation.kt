package com.example.stretchy.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.navigation.screens.MainScreenView
import com.example.stretchy.navigation.screens.CreatingExerciseScreen
import com.example.stretchy.navigation.screens.ExerciseScreen
import com.example.stretchy.navigation.screens.Screen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ExercisesListScreen.route) {
        composable(route = Screen.ExercisesListScreen.route) {
            MainScreenView(navController = navController)
        }
        composable(route = Screen.ExerciseCreatorScreen.route) {
            CreatingExerciseScreen()
        }
        composable(route = Screen.ExerciseScreen.route){
            ExerciseScreen()
        }
    }
}