package com.example.stretchy.features.datatransport

import com.example.stretchy.features.traininglist.di.TrainingListScope
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataTransportModule {
    @TrainingListScope
    @Provides
    fun provideDataImporter(repository: Repository) = DataImporterImporterImpl(repository)

    @TrainingListScope
    @Provides
    fun provideDataExporter(repository: Repository) = DataExporterExporterImpl(repository)
}