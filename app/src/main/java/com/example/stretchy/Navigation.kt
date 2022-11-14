package com.example.stretchy

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.features.createtraining.ui.composable.CreateTrainingComposable
import com.example.stretchy.features.traininglist.ui.composable.TrainingsComposable
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingComposable

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ExercisePlansScreen.route) {
        composable(route = Screen.ExercisePlansScreen.route) {
            TrainingsComposable(navController = navController)
        }
        composable(route = Screen.ExerciseCreatorScreen.route) {
            CreateTrainingComposable(navController = navController)
        }
        composable(route = Screen.ExerciseScreen.route) {
            ExecuteTrainingComposable()
        }
    }
}