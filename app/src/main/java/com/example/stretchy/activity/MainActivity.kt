package com.example.stretchy.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.stretchy.Navigation
import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.extensions.daggerViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var createTrainingViewModelProvider: Provider<CreateTrainingViewModel>
    private val createTrainingViewModel by daggerViewModel(this) { createTrainingViewModelProvider }

    @Inject
    lateinit var trainingLIstViewModelProvider: Provider<TrainingListViewModel>
    private val trainingListViewModel by daggerViewModel(this) { trainingLIstViewModelProvider }

    private lateinit var component: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = ActivityComponent.create(this)

        setContent {
            Navigation(
                component,
                createTrainingViewModel,
                trainingListViewModel
            )
        }
    }

   @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Navigation(
            component,
            createTrainingViewModel,
            trainingListViewModel
        )
    }
}