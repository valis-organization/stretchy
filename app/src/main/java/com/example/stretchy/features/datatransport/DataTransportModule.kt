package com.example.stretchy.features.datatransport

import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class DataTransportModule {
    @Provides
    fun provideDataImporter(repository: Repository) = DataImporterImporterImpl(repository)

    @Provides
    fun provideDataExporter(repository: Repository) = DataExporterExporterImpl(repository)
}