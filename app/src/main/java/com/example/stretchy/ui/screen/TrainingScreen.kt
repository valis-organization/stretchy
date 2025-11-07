package com.example.stretchy.ui.screen

import androidx.compose.runtime.Composable
import com.example.stretchy.Screen
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.navigation.Navigation

@Composable
fun TrainingScreen(
    soundPlayer: SoundPlayer,
    grantWritePermission: () -> Unit,
    grantReadPermission: () -> Unit,
    hideBottomNavBar: () -> Unit,
    showBottomNavBar: () -> Unit,
) {
    Navigation(
        soundPlayer = soundPlayer,
        onExportClick = grantWritePermission,
        onImportClick = grantReadPermission,
        startDestination = Screen.TrainingListScreen.route,
        hideBottomNavBar = hideBottomNavBar,
        showBottomNavBar = showBottomNavBar,
        trainingType = TrainingType.BODYWEIGHT
    )
}
