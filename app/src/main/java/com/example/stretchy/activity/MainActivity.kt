package com.example.stretchy.activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.permission.PermissionManager
import com.example.stretchy.permission.rememberStoragePermissionState
import com.example.stretchy.ui.navigation.BottomNavBar
class MainActivity : ComponentActivity() {
    private lateinit var activityComponent: ActivityComponent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = ActivityComponent.create(this)
        setContent {
            // Remember permission manager & compose state
            val permissionManager = remember { PermissionManager(this) }
            val storagePermissionState = rememberStoragePermissionState(permissionManager)
            BottomNavBar(activityComponent = activityComponent, storagePermissionState = storagePermissionState)
        }
    }
}
