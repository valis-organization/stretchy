package com.example.stretchy.features.datatransport

import com.example.stretchy.features.traininglist.di.TrainingListScope
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class DataTransportModule {
    @TrainingListScope
    @Provides
    fun provideDataImporter(repository: Repository) = DataImporterImpl(repository)

    @TrainingListScope
    @Provides
    fun provideDataExporter(repository: Repository) = DataExporterImpl(repository)
}