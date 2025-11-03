package com.example.stretchy.ui.screen

import androidx.compose.runtime.Composable
import com.example.stretchy.Screen
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.navigation.Navigation

@Composable
fun TrainingScreen(
    activityComponent: ActivityComponent,
    grantWritePermission: () -> Unit,
    grantReadPermission: () -> Unit,
    hideBottomNavBar: () -> Unit,
    showBottomNavBar: () -> Unit,
) {
    Navigation(
        activityComponent = activityComponent,
        onExportClick = grantWritePermission,
        onImportClick = grantReadPermission,
        startDestination = Screen.TrainingListScreen.route,
        hideBottomNavBar = hideBottomNavBar,
        showBottomNavBar = showBottomNavBar,
        trainingType = TrainingType.BODYWEIGHT
    )
}
