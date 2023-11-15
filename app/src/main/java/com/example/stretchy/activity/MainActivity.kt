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
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.stretchy.Navigation
import com.example.stretchy.activity.di.ActivityComponent


class MainActivity : ComponentActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = ActivityComponent.create(this)
        setContent {
            BottomNavigationBar(activityComponent, grantPermissions = { grantPermissions(it) })
            var showExportPermissionDialog by remember { mutableStateOf(false) }
            var showImportPermissionDialog by remember { mutableStateOf(false) }
            Navigation(
                activityComponent,
                onExportClick = { showExportPermissionDialog = true },
                onImportClick = { showImportPermissionDialog = true }
            )
            if (showExportPermissionDialog) {
                ShowPermissionDialogue(
                    permission = WRITE_EXTERNAL_STORAGE,
                    onDismissListener = { showExportPermissionDialog = false })
            } else if (showImportPermissionDialog) {
                ShowPermissionDialogue(
                    permission = READ_EXTERNAL_STORAGE,
                    onDismissListener = { showImportPermissionDialog = false })
            }
        }
    }

    @Composable
    private fun ShowPermissionDialogue(permission: String, onDismissListener: () -> Unit) {
        GrantPermissions(
            permission,
            this,
            this,
            onPermissionRequest = {
                startActivity(it)
            },
            onDismissClick = {
                onDismissListener()
            }
        )
    }
}


