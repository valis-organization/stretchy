package com.example.stretchy.activity

import android.os.Bundle
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