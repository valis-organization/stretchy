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
}


