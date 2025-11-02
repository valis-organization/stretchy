package com.example.stretchy.navigation

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.Navigation
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.database.data.TrainingType


@Composable
fun BottomNavigationBar(
    activityComponent: ActivityComponent,
    grantPermissions: (permission: String) -> Unit
) {
    val navController = rememberNavController()
    var showBottomNavBar by remember { (mutableStateOf(true)) }

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

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNavBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = shrinkVertically(),
            ) {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    screens.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(text = stringResource(id = screen.labelRes)) },
                            selected = currentRoute == screen.route,
                            onClick = { navController.navigate(screen.route) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.StretchingListScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.StretchingListScreen.route) {
                Navigation(
                    activityComponent,
                    { grantPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) },
                    { grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) },
                    Screen.StretchingListScreen.route,
                    hideBottomNavBar = { showBottomNavBar = false },
                    showBottomNavBar = { showBottomNavBar = true },
                    trainingType = TrainingType.STRETCH
                )
            }
            composable(Screen.MetaTrainingScreen.route) {
                // TODO: Meta training screen content
            }
            composable(Screen.TrainingListScreen.route) {
                Navigation(
                    activityComponent,
                    { grantPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) },
                    { grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) },
                    Screen.TrainingListScreen.route,
                    hideBottomNavBar = { showBottomNavBar = false },
                    showBottomNavBar = { showBottomNavBar = true },
                    trainingType = TrainingType.BODYWEIGHT
                )
            }
        }
    }
}
