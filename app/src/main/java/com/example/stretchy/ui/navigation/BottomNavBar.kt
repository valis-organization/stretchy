package com.example.stretchy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.design.components.LocalDesignColors
import com.example.stretchy.design.components.StretchingTheme
import com.example.stretchy.design.components.TrainingTheme
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
    soundPlayer: SoundPlayer
) {
    val navController = rememberNavController()
    var showBottomNavBar by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screens = listOf(
        BottomNavScreen(
            R.string.stretching,
            painterResource(id = R.drawable.ic_stretching),
            Screen.StretchingListScreen.route
        ),
        BottomNavScreen(
            R.string.meta_training,
            painterResource(id = R.drawable.ic_meta_training),
            Screen.MetaTrainingScreen.route
        ),
        BottomNavScreen(
            R.string.training,
            painterResource(id = R.drawable.ic_training),
            Screen.TrainingListScreen.route
        ),
    )

    // Determine theme based on current route and wrap content
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
                    contentColor = Color.Gray
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

