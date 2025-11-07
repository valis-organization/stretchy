package com.example.stretchy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stretchy.Screen
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.NewTrainingEditScreen
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingScreen
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.TrainingListScreen

@Composable
fun Navigation(
    soundPlayer: SoundPlayer,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    startDestination: String,
    hideBottomNavBar: () -> Unit,
    showBottomNavBar: () -> Unit,
    trainingType: TrainingType
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    handleBottomNavBarVisibility(navBackStackEntry, hideBottomNavBar, showBottomNavBar)

    // Centralized navigation handler
    val navigationViewModel: NavigationViewModel = viewModel()
    HandleNavigationEvents(
        navEvents = navigationViewModel.navEvents,
        navController = navController
    )

    NavHost(navController = navController, startDestination = startDestination) {
        composable(
            route = Screen.TrainingListScreen.route,
        ) {
            val vm: TrainingListViewModel = hiltViewModel()
            vm.setTrainingType(TrainingType.BODYWEIGHT)
            TrainingListScreen(
                viewModel = vm,
                navigationViewModel = navigationViewModel,
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                trainingType = TrainingType.BODYWEIGHT
            )
        }

        composable(
            route = Screen.StretchingListScreen.route
        ) {
            val vm: TrainingListViewModel = hiltViewModel()
            vm.setTrainingType(TrainingType.STRETCH)
            TrainingListScreen(
                viewModel = vm,
                navigationViewModel = navigationViewModel,
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                trainingType = TrainingType.STRETCH
            )
        }
        composable(
            route = Screen.ExerciseCreatorScreen.route,
            arguments = listOf(
                navArgument("id") { defaultValue = "-1" },
                navArgument("trainingType") { defaultValue = "STRETCH" })
        ) {
            val vm: CreateOrEditTrainingViewModel = hiltViewModel()
            NewTrainingEditScreen(
                navController = navController,
                viewModel = vm
            )
        }
        composable(
            route = Screen.ExecuteTrainingScreen.route,
            arguments = listOf(navArgument("id") { defaultValue = "-1" })
        ) {
            val vm: ExecuteTrainingViewModel = hiltViewModel()
            ExecuteTrainingScreen(
                vm,
                soundPlayer,
                navController
            )
        }
    }
}


private fun handleBottomNavBarVisibility(
    navBackStackEntry: NavBackStackEntry?, hideBottomNavBar: () -> Unit,
    showBottomNavBar: () -> Unit
) {
    when (navBackStackEntry?.destination?.route) {
        Screen.ExerciseCreatorScreen.route -> {
            hideBottomNavBar()
        }
        Screen.StretchingListScreen.route -> {
            showBottomNavBar()
        }
        Screen.TrainingListScreen.route -> {
            showBottomNavBar()
        }
        Screen.ExecuteTrainingScreen.route -> {
            hideBottomNavBar()
        }
    }
}
