package com.example.stretchy.activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
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
        setContent {
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
