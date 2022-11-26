package com.example.stretchy.activity.di

import com.example.stretchy.activity.di.scope.ActivityScope
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class ViewModelsModule {
    @ActivityScope
    @Provides
    fun provideExecuteTrainingViewModel(repository: Repository) =
        ExecuteTrainingViewModel(repository)

    @ActivityScope
    @Provides
    fun provideCreateTrainingViewModel(repository: Repository) = CreateTrainingViewModel(repository)

    @ActivityScope
    @Provides
    fun provideTrainingListViewModel(repository: Repository) = TrainingListViewModel(repository)
}