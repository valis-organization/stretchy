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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.Navigation
import com.example.stretchy.activity.di.ActivityComponent


class MainActivity : ComponentActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = ActivityComponent.create(this)
        setContent {
            BottomNavigationBar()
      /*      Navigation(
                activityComponent,
                {
                    grantPermissions(WRITE_EXTERNAL_STORAGE)
                },
                {
                    grantPermissions(READ_EXTERNAL_STORAGE)
                }
            )*/
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Navigation(
            activityComponent,
            {
                grantPermissions(WRITE_EXTERNAL_STORAGE)
            },
            {
                grantPermissions(READ_EXTERNAL_STORAGE)
            }
        )
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

        val screens = listOf(
            BottomNavScreen("Screen 1", Icons.Default.Home, "screen1"),
            BottomNavScreen("Screen 2", Icons.Default.Search, "screen2"),
            BottomNavScreen("Screen 3", Icons.Default.Person, "screen3"),
        )

        Scaffold(
            bottomBar = {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    screens.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                            label = { Text(text = screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route)
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "screen1",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("screen1") {
                    Navigation(
                        activityComponent,
                        {
                            grantPermissions(WRITE_EXTERNAL_STORAGE)
                        },
                        {
                            grantPermissions(READ_EXTERNAL_STORAGE)
                        }
                    )
                }
                composable("screen2") {

                }
                composable("screen3") {

                }
            }
        }
    }
}

data class BottomNavScreen(val label: String, val icon: ImageVector, val route: String)

