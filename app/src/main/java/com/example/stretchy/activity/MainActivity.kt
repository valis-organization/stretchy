package com.example.stretchy.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.stretchy.Navigation
import com.example.stretchy.app.di.ActivityComponent
import com.example.stretchy.repository.Repository
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityComponent.create(this)

        setContent {
            Navigation()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Navigation()
    }
}