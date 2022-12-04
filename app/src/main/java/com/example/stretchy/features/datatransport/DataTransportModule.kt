package com.example.stretchy.features.datatransport

import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class DataTransportModule {
    @Provides
    fun provideImport(repository: Repository) = Import(repository)

    @Provides
    fun provideExport(repository: Repository) = Export(repository)
}