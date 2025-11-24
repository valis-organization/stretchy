package com.example.stretchy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stretchy.Screen
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.design.components.StretchingTheme
import com.example.stretchy.design.components.TrainingTheme
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.NewTrainingEditScreen
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.executetraining.ui.composable.ExecuteTrainingScreen
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.TrainingListScreen
import com.example.stretchy.navigation.BottomNavScreen
import com.example.stretchy.navigation.HandleNavigationEvents
import com.example.stretchy.navigation.NavigationViewModel
import com.example.stretchy.permission.StoragePermissionState
import com.example.stretchy.ui.screen.MetaTrainingBottomBarScreen

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
                AnimatedVisibility(
                    visible = shouldShowBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = shrinkVertically(),
                ) {
                    BottomAppBar(
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        screens.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(text = stringResource(id = screen.labelRes)) },
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
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
                    TrainingListScreen(
                        viewModel = vm,
                        navigationViewModel = navigationViewModel,
                        onExportClick = { storagePermissionState.requestWrite() },
                        onImportClick = { storagePermissionState.requestRead() },
                        trainingType = TrainingType.STRETCH
                    )
                }

                composable(Screen.TrainingListScreen.route) {
                    val vm: TrainingListViewModel = hiltViewModel()
                    vm.setTrainingType(TrainingType.BODYWEIGHT)
                    TrainingListScreen(
                        viewModel = vm,
                        navigationViewModel = navigationViewModel,
                        onExportClick = { storagePermissionState.requestWrite() },
                        onImportClick = { storagePermissionState.requestRead() },
                        trainingType = TrainingType.BODYWEIGHT
                    )
                }

                composable(Screen.MetaTrainingScreen.route) {
                    MetaTrainingBottomBarScreen()
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
