package com.example.stretchy.features.createtraining.ui.di

import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.BreakDb
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class CreateTrainingModule {
    @Provides
    fun provideCreateTrainingViewModel(repository: Repository, trainingId: Long, breakDb: BreakDb) =
        CreateOrEditTrainingViewModel(repository, trainingId, breakDb)
}