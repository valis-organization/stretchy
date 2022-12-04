package com.example.stretchy.features.traininglist.di

import com.example.stretchy.features.datatransport.DataExporterExporterImpl
import com.example.stretchy.features.datatransport.DataImporterImporterImpl
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class TrainingListModule {
    @Provides
    fun provideTrainingListViewModel(
        repository: Repository,
        dataImporterImpl: DataImporterImporterImpl,
        dataExporterImpl: DataExporterExporterImpl
    ) =
        TrainingListViewModel(repository, dataImporterImpl, dataExporterImpl)
}