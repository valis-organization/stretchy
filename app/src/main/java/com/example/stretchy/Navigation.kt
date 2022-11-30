package com.example.stretchy

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.CreateTrainingComposable
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingComposable
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.TrainingListComposable

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
        composable(
            route = "trainingList?id={id}",
            arguments = listOf(navArgument("id") { defaultValue = "-1" })
        ) {
            val trainingId = it.arguments?.getString("id")
            ExecuteTrainingComposable(viewModel = executeTrainingViewModel, trainingId!!)
        }
    }
}