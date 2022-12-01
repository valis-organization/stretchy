package com.example.stretchy.activity.di

import com.example.stretchy.features.datatransport.Export
import com.example.stretchy.features.datatransport.Import
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