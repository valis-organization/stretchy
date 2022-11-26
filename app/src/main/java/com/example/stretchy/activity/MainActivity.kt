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
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var createTrainingViewModelProvider: Provider<CreateTrainingViewModel>
    private val createTrainingViewModel by daggerViewModel { createTrainingViewModelProvider }

    @Inject
    lateinit var trainingLIstViewModelProvider: Provider<TrainingListViewModel>
    private val trainingListViewModel by daggerViewModel { trainingLIstViewModelProvider }

    @Inject
    lateinit var executeTrainingViewModelProvider: Provider<ExecuteTrainingViewModel>
    private val executeTrainingViewModel by daggerViewModel { executeTrainingViewModelProvider }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityComponent.create(this)

        setContent {
            Navigation(createTrainingViewModel, executeTrainingViewModel, trainingListViewModel)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Navigation(createTrainingViewModel, executeTrainingViewModel, trainingListViewModel)
    }
}