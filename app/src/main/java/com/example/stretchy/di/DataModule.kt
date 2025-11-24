package com.example.stretchy.di

import com.example.stretchy.features.domain.usecases.CopyTrainingRepoAdapter
import com.example.stretchy.features.domain.usecases.CreateTrainingRepoAdapter
import com.example.stretchy.features.domain.usecases.DeleteTrainingRepoAdapter
import com.example.stretchy.features.domain.usecases.EditTrainingRepoAdapter
import com.example.stretchy.features.domain.usecases.FetchTrainingByIdRepoAdapter
import com.example.stretchy.features.domain.usecases.FetchTrainingListRepoAdapter
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Data Module - Repository Adapter Layer
 *
 * Clean Architecture: Contains repository adapters that bridge UI and Repository
 * These are simple data operations without business logic
 *
 * Note: These adapters work with TrainingWithActivity (repository models)
 * Business logic should use domain models in DomainModule use cases
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // ========= REPOSITORY ADAPTERS - Data Layer Operations =========

    @Provides
    @Singleton
    fun provideCreateTrainingRepoAdapter(repository: Repository): CreateTrainingRepoAdapter =
        CreateTrainingRepoAdapter(repository)

    @Provides
    @Singleton
    fun provideEditTrainingRepoAdapter(repository: Repository): EditTrainingRepoAdapter =
        EditTrainingRepoAdapter(repository)

    @Provides
    @Singleton
    fun provideDeleteTrainingRepoAdapter(repository: Repository): DeleteTrainingRepoAdapter =
        DeleteTrainingRepoAdapter(repository)

    @Provides
    @Singleton
    fun provideFetchTrainingListRepoAdapter(repository: Repository): FetchTrainingListRepoAdapter =
        FetchTrainingListRepoAdapter(repository)

    @Provides
    @Singleton
    fun provideFetchTrainingByIdRepoAdapter(repository: Repository): FetchTrainingByIdRepoAdapter =
        FetchTrainingByIdRepoAdapter(repository)

    @Provides
    @Singleton
    fun provideCopyTrainingRepoAdapter(repository: Repository): CopyTrainingRepoAdapter =
        CopyTrainingRepoAdapter(repository)
}
