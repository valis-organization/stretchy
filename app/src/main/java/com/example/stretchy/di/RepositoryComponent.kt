package com.example.stretchy.di

import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import dagger.Component

@Component(modules = [DataBaseModule::class])
interface RepositoryComponent {
    fun inject(executeTrainingViewModel: ExecuteTrainingViewModel)
    fun inject(trainingViewModel: TrainingListViewModel)
}