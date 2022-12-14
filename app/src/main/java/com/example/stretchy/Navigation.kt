package com.example.stretchy

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.extensions.daggerViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.CreateTrainingComposable
import com.example.stretchy.features.createtraining.ui.di.CreateTrainingComponent
import com.example.stretchy.features.executetraining.di.ExecuteTrainingComponent
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingComposable
import com.example.stretchy.features.traininglist.di.TrainingListComponent
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.TrainingListComposable

@Composable
fun Navigation(
    activityComponent: ActivityComponent,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.TrainingListScreen.route) {
        composable(route = Screen.TrainingListScreen.route) {
            val vm = createTrainingListViewModel(
                activityComponent,
                LocalViewModelStoreOwner.current!!
            )
            TrainingListComposable(
                navController = navController,
                viewModel = vm,
                onExportClick = onExportClick,
                onImportClick = onImportClick
            )
        }
        composable(route = Screen.ExerciseCreatorScreen.route) {
            val vm = createCreateTrainingViewModel(
                activityComponent,
                LocalViewModelStoreOwner.current!!
            )
            CreateTrainingComposable(
                navController = navController,
                viewModel = vm
            )
        }
        composable(
            route = "executeTraining?id={id}",
            arguments = listOf(navArgument("id") { defaultValue = "-1" })
        ) {
            val trainingId = it.arguments?.getString("id")!!.toLong()
            val component = ExecuteTrainingComponent.create(activityComponent, trainingId)
            val vm = createExecuteTrainingViewModel(
                component,
                activityComponent.activity(),
                LocalViewModelStoreOwner.current!!,
            )
            ExecuteTrainingComposable(vm, activityComponent.speaker(), navController)
        }
    }
}

private fun createExecuteTrainingViewModel(
    executeTrainingComponent: ExecuteTrainingComponent,
    componentActivity: ComponentActivity,
    viewModelStoreOwner: ViewModelStoreOwner,
): ExecuteTrainingViewModel {
    val provider = executeTrainingComponent.viewModelProvider()
    val vm by componentActivity.daggerViewModel(owner = viewModelStoreOwner) { provider }
    return vm
}

private fun createCreateTrainingViewModel(
    activityComponent: ActivityComponent,
    viewModelStoreOwner: ViewModelStoreOwner,
): CreateTrainingViewModel {
    val component = CreateTrainingComponent.create(activityComponent)
    val provider = component.viewModelProvider()
    val vm by activityComponent.activity()
        .daggerViewModel(owner = viewModelStoreOwner) { provider }
    return vm
}

private fun createTrainingListViewModel(
    activityComponent: ActivityComponent,
    viewModelStoreOwner: ViewModelStoreOwner,
): TrainingListViewModel {
    val component = TrainingListComponent.create(activityComponent)
    val provider = component.viewModelProvider()
    val vm by activityComponent.activity()
        .daggerViewModel(owner = viewModelStoreOwner) { provider }
    return vm
}