package com.example.stretchy.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.BottomNavScreen
import com.example.stretchy.Navigation
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.database.data.TrainingType


class MainActivity : ComponentActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = ActivityComponent.create(this)
        setContent {
            BottomNavigationBar()
        }
    }

    private fun grantPermissions(permission: String) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + applicationContext.packageName)
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission
                ),
                100
            )
        }
    }

    @Composable
    fun BottomNavigationBar() {
        val navController = rememberNavController()
        var showBottomNavBar by remember { (mutableStateOf(true)) }

        val screens = listOf(
            BottomNavScreen(
                stringResource(id = R.string.stretching),
                painterResource(id = R.drawable.ic_stretching),
                Screen.StretchingListScreen.route
            ),
            BottomNavScreen(
                stringResource(id = R.string.meta_training),
                painterResource(id = R.drawable.ic_meta_training),
                stringResource(id = R.string.meta_training)
            ),
            BottomNavScreen(
                stringResource(id = R.string.training),
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
                    content = {
                        BottomNavigation {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            screens.forEach { screen ->
                                BottomNavigationItem(
                                    icon = { Icon(screen.icon, contentDescription = "") },
                                    label = { Text(text = screen.label) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route)
                                    }
                                )
                            }
                        }
                    })
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
                        {
                            grantPermissions(WRITE_EXTERNAL_STORAGE)
                        },
                        {
                            grantPermissions(READ_EXTERNAL_STORAGE)
                        },
                        Screen.StretchingListScreen.route,
                        hideBottomNavBar = { showBottomNavBar = false },
                        showBottomNavBar = {showBottomNavBar =true},
                        trainingType = TrainingType.STRETCH
                    )
                }
                composable(getString(R.string.meta_training)) {

                }
                composable(Screen.TrainingListScreen.route) {
                    Navigation(
                        activityComponent,
                        {
                            grantPermissions(WRITE_EXTERNAL_STORAGE)
                        },
                        {
                            grantPermissions(READ_EXTERNAL_STORAGE)
                        },
                        Screen.TrainingListScreen.route,
                        hideBottomNavBar = { showBottomNavBar = false },
                        showBottomNavBar = {showBottomNavBar =true},
                        trainingType = TrainingType.BODYWEIGHT
                    )
                }
            }
        }
    }
}



