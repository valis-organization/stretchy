package com.example.stretchy.features.traininglist.di

import com.example.stretchy.features.datatransport.DataExporter
import com.example.stretchy.features.datatransport.DataImporter
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class TrainingListModule {
    @Provides
    fun provideTrainingListViewModel(
        repository: Repository,
        dataImporter: DataImporter,
        dataExporter: DataExporter
    ) =
        TrainingListViewModel(repository, dataImporter, dataExporter)
}