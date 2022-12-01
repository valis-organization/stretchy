package com.example.stretchy.activity

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.stretchy.Navigation
import com.example.stretchy.activity.di.ActivityComponent


class MainActivity : ComponentActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = ActivityComponent.create(this)
        if (SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent().action = ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            }
        }
        ActivityComponent.create(this)

        setContent {
            Navigation(activityComponent)
        }
    }

   @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Navigation(activityComponent)
    }
}