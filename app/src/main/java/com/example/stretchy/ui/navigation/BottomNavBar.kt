package com.example.stretchy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.Screen
import com.example.stretchy.design.components.StretchingTheme
import com.example.stretchy.design.components.TrainingTheme
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.navigation.BottomNavScreen
import com.example.stretchy.permission.StoragePermissionState
import com.example.stretchy.ui.screen.MetaTrainingScreen
import com.example.stretchy.ui.screen.StretchingScreen
import com.example.stretchy.ui.screen.TrainingScreen


@Composable
fun BottomNavBar(
    storagePermissionState: StoragePermissionState,
    soundPlayer: SoundPlayer,
    screens: List<BottomNavScreen>
) {
    val navController = rememberNavController()
    var showBottomNavBar by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val content: @Composable () -> Unit = {
        Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNavBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = shrinkVertically(),
            ) {
                BottomNavigation(
                    backgroundColor = Color.White,
                    contentColor = Color.Gray,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    screens.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(text = stringResource(id = screen.labelRes)) },
                            selected = currentRoute == screen.route,
                            selectedContentColor = when (screen.route) {
                                Screen.StretchingListScreen.route -> Color(0xFF4CAF50) // Green for stretching
                                Screen.TrainingListScreen.route -> Color(0xFFFF9800) // Orange for training
                                else -> Color(0xFF4CAF50) // Default green
                            },
                            unselectedContentColor = Color.Gray,
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
            composable(Screen.StretchingListScreen.route) {
                StretchingScreen(
                    soundPlayer = soundPlayer,
                    grantWritePermission = { storagePermissionState.requestWrite() },
                    grantReadPermission = { storagePermissionState.requestRead() },
                    hideBottomNavBar = { showBottomNavBar = false },
                    showBottomNavBar = { showBottomNavBar = true }
                )
            }
            composable(Screen.MetaTrainingScreen.route) {
                MetaTrainingScreen()
            }
            composable(Screen.TrainingListScreen.route) {
                TrainingScreen(
                    soundPlayer = soundPlayer,
                    grantWritePermission = { storagePermissionState.requestWrite() },
                    grantReadPermission = { storagePermissionState.requestRead() },
                    hideBottomNavBar = { showBottomNavBar = false },
                    showBottomNavBar = { showBottomNavBar = true }
                )
            }
        }
    }
    }

    // Apply appropriate theme based on current route
    when (currentRoute) {
        Screen.StretchingListScreen.route -> StretchingTheme { content() }
        Screen.TrainingListScreen.route -> TrainingTheme { content() }
        Screen.MetaTrainingScreen.route -> StretchingTheme { content() } // Default to stretching theme for meta training
        else -> StretchingTheme { content() } // Default theme
    }
}
