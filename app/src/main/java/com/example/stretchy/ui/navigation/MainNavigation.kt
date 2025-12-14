package com.example.stretchy.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stretchy.Screen
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.design.components.ActivityListScreen
import com.example.stretchy.design.components.StretchingTheme
import com.example.stretchy.design.components.TrainingTheme
import com.example.stretchy.design.components.toActivityItem
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.NewTrainingEditScreen
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingScreen
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.navigation.BottomNavScreen
import com.example.stretchy.navigation.HandleNavigationEvents
import com.example.stretchy.navigation.NavigationViewModel
import com.example.stretchy.permission.StoragePermissionState

/**
 * Main Navigation Component
 *
 * Single NavHost architecture that handles:
 * - Bottom navigation tabs with state preservation
 * - Full-screen routes (exercise creator, execute training)
 * - Automatic bottom bar visibility management
 * - Proper theme switching per route
 *
 * Replaces the old double NavHost anti-pattern
 */
@Composable
fun MainNavigation(
    storagePermissionState: StoragePermissionState,
    soundPlayer: SoundPlayer,
    screens: List<BottomNavScreen>
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Centralized navigation handler
    val navigationViewModel: NavigationViewModel = viewModel()
    HandleNavigationEvents(
        navEvents = navigationViewModel.navEvents,
        navController = navController
    )

    // Handle bottom bar visibility based on current route
    val shouldShowBottomBar = when (currentRoute) {
        Screen.ExerciseCreatorScreen.route, Screen.ExecuteTrainingScreen.route -> false
        else -> true
    }

    val contentUI: @Composable () -> Unit = {
        Scaffold(
            bottomBar = {
                AppBottomBar(
                    shouldShowBottomBar = shouldShowBottomBar,
                    screens = screens,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.StretchingListScreen.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .statusBarsPadding()
            ) {
                // Bottom Bar Tab Routes
                composable(Screen.StretchingListScreen.route) {
                    val vm: TrainingListViewModel = hiltViewModel()
                    vm.setTrainingType(TrainingType.STRETCH)
                    
                    val state by vm.uiState.collectAsState()
                    when (val uiState = state) {
                        is TrainingListUiState.Loaded -> {
                            ActivityListScreen(
                                activities = uiState.trainings.map { it.toActivityItem() },
                                trainingType = TrainingType.STRETCH,
                                onAdd = {
                                    navigationViewModel.navigateToCreateTraining(trainingType = TrainingType.STRETCH.toString())
                                },
                                onActivityClick = { activityItem ->
                                    navigationViewModel.navigateToExecuteTraining(activityItem.id)
                                },
                                onActivityEdit = { activityItem ->
                                    navigationViewModel.navigateToEditTraining(trainingId = activityItem.id, trainingType = TrainingType.STRETCH.toString())
                                },
                                onActivityDelete = { activityItem ->
                                    val trainingToDelete = uiState.trainings.find { it.id == activityItem.id }
                                    trainingToDelete?.let { vm.deleteTraining(it) }
                                },
                                onExportClick = { storagePermissionState.requestWrite() },
                                onImportClick = { storagePermissionState.requestRead() },
                                onPerformExport = { vm.export() },
                                onPerformImport = { vm.import() }
                            )
                        }
                        is TrainingListUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else -> { // Empty or Error
                            ActivityListScreen(
                                activities = emptyList(),
                                trainingType = TrainingType.STRETCH,
                                onAdd = {
                                    navigationViewModel.navigateToCreateTraining(trainingType = TrainingType.STRETCH.toString())
                                },
                                onExportClick = { storagePermissionState.requestWrite() },
                                onImportClick = { storagePermissionState.requestRead() },
                                onPerformExport = { vm.export() },
                                onPerformImport = { vm.import() }
                            )
                        }
                    }
                }

                composable(Screen.TrainingListScreen.route) {
                    val vm: TrainingListViewModel = hiltViewModel()
                    vm.setTrainingType(TrainingType.BODYWEIGHT)
                    
                    val state by vm.uiState.collectAsState()
                    when (val uiState = state) {
                        is TrainingListUiState.Loaded -> {
                            ActivityListScreen(
                                activities = uiState.trainings.map { it.toActivityItem() },
                                trainingType = TrainingType.BODYWEIGHT,
                                onAdd = {
                                    navigationViewModel.navigateToCreateTraining(trainingType = TrainingType.BODYWEIGHT.toString())
                                },
                                onActivityClick = { activityItem ->
                                    navigationViewModel.navigateToExecuteTraining(activityItem.id)
                                },
                                onActivityEdit = { activityItem ->
                                    navigationViewModel.navigateToEditTraining(trainingId = activityItem.id, trainingType = TrainingType.BODYWEIGHT.toString())
                                },
                                onActivityDelete = { activityItem ->
                                    val trainingToDelete = uiState.trainings.find { it.id == activityItem.id }
                                    trainingToDelete?.let { vm.deleteTraining(it) }
                                },
                                onExportClick = { storagePermissionState.requestWrite() },
                                onImportClick = { storagePermissionState.requestRead() },
                                onPerformExport = { vm.export() },
                                onPerformImport = { vm.import() }
                            )
                        }
                        is TrainingListUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else -> { // Empty or Error
                            ActivityListScreen(
                                activities = emptyList(),
                                trainingType = TrainingType.BODYWEIGHT,
                                onAdd = {
                                    navigationViewModel.navigateToCreateTraining(trainingType = TrainingType.BODYWEIGHT.toString())
                                },
                                onExportClick = { storagePermissionState.requestWrite() },
                                onImportClick = { storagePermissionState.requestRead() },
                                onPerformExport = { vm.export() },
                                onPerformImport = { vm.import() }
                            )
                        }
                    }
                }

                composable(Screen.MetaTrainingScreen.route) {
                    // MetaTrainingBottomBarScreen removed as requested
                }

                // Full-screen Routes (no bottom bar)
                composable(
                    route = Screen.ExerciseCreatorScreen.route,
                    arguments = listOf(
                        navArgument("id") { defaultValue = "-1" },
                        navArgument("trainingType") { defaultValue = "STRETCH" }
                    )
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
    }

    // Apply appropriate theme based on current route
    when (currentRoute) {
        Screen.StretchingListScreen.route -> StretchingTheme { contentUI() }
        Screen.TrainingListScreen.route -> TrainingTheme { contentUI() }
        Screen.MetaTrainingScreen.route -> StretchingTheme { contentUI() } // Default to stretching theme for meta training
        else -> StretchingTheme { contentUI() } // Default theme
    }
}
