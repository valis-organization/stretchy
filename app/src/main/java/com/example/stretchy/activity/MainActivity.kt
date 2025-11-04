package com.example.stretchy.activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.permission.PermissionManager
import com.example.stretchy.permission.rememberStoragePermissionState
import com.example.stretchy.ui.navigation.BottomNavBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var soundPlayer: SoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge and make status bar transparent
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Make status bar transparent
            SideEffect {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = false // Dark status bar content for light themes
                }
            }

            // Remember permission manager & compose state
            val permissionManager = remember { PermissionManager(this) }
            val storagePermissionState = rememberStoragePermissionState(permissionManager)
            BottomNavBar(
                storagePermissionState = storagePermissionState,
                soundPlayer = soundPlayer
            )
        }
    }
}
