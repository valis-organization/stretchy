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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.BottomNavScreen
import com.example.stretchy.Navigation
import com.example.stretchy.R
import com.example.stretchy.activity.di.ActivityComponent


class MainActivity : ComponentActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = ActivityComponent.create(this)
        setContent {
            BottomNavigationBar()
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
            BottomNavScreen(
                stringResource(id = R.string.stretching),
                painterResource(id = R.drawable.ic_stretching),
                stringResource(id = R.string.stretching)
            ),
            BottomNavScreen(
                stringResource(id = R.string.meta_training),
                painterResource(id = R.drawable.ic_meta_training),
                stringResource(id = R.string.meta_training)
            ),
            BottomNavScreen(
                stringResource(id = R.string.training),
                painterResource(id = R.drawable.ic_training),
                stringResource(id = R.string.training)
            ),
        )

        Scaffold(
            bottomBar = {
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
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = stringResource(id = R.string.stretching),
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(getString(R.string.stretching)) {
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
                composable(getString(R.string.meta_training)) {

                }
                composable(getString(R.string.training)) {

                }
            }
        }
    }
}



