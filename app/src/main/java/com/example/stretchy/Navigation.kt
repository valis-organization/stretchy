package com.example.stretchy

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.CreateTrainingComposable
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.traininglist.ui.composable.TrainingListComposable
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingComposable
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel

@Composable
fun Navigation(
    createTrainingViewModel: CreateTrainingViewModel,
    executeTrainingViewModel: ExecuteTrainingViewModel,
    trainingListViewModel: TrainingListViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ExercisePlansScreen.route) {
        composable(route = Screen.ExercisePlansScreen.route) {
            TrainingListComposable(
                navController = navController,
                viewModel = trainingListViewModel
            )
        }
        composable(route = Screen.ExerciseCreatorScreen.route) {
            CreateTrainingComposable(
                navController = navController,
                viewModel = createTrainingViewModel
            )
        }
        composable(route = Screen.ExerciseScreen.route) {
            ExecuteTrainingComposable(viewModel = executeTrainingViewModel)
        }
    }
}