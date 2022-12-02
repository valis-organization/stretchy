package com.example.stretchy

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.extensions.daggerViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.CreateTrainingComposable
import com.example.stretchy.features.executetraining.di.ExecuteTrainingComponent
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingComposable
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.TrainingListComposable

@Composable
fun Navigation(
    activityComponent: ActivityComponent,
    createTrainingViewModel: CreateTrainingViewModel,
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
            val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }

            val trainingId = it.arguments?.getString("id")!!.toLong()
            val component = ExecuteTrainingComponent.create(activityComponent, trainingId)
            val provider = component.executeTrainingVmProvider()
            val vm by activityComponent.activity().daggerViewModel(owner = viewModelStoreOwner) { provider }
            ExecuteTrainingComposable(vm)
        }
    }
}