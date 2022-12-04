package com.example.stretchy.features.traininglist.di

import com.example.stretchy.features.datatransport.Export
import com.example.stretchy.features.datatransport.Import
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class TrainingListModule {
    @Provides
    fun provideTrainingListViewModel(repository: Repository, import: Import, export: Export) =
        TrainingListViewModel(repository,import,export)
}