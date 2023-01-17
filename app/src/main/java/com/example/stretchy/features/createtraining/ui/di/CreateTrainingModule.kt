package com.example.stretchy.features.createtraining.ui.di

import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class CreateTrainingModule {
    @Provides
    fun provideCreateTrainingViewModel(repository: Repository, trainingId: Long) =
        CreateTrainingViewModel(repository, trainingId)
}