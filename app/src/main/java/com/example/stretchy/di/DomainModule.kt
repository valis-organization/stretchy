package com.example.stretchy.di

import com.example.stretchy.features.createtraining.domain.BreakManagementUseCase
import com.example.stretchy.features.domain.usecases.CreateTrainingDomainUseCase
import com.example.stretchy.features.domain.usecases.EditTrainingDomainUseCase
import com.example.stretchy.features.domain.usecases.FetchTrainingDomainUseCase
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Domain Module - Business Logic Layer
 *
 * Clean Architecture: Contains only domain use cases with business logic
 *
 * Naming Convention:
 * - Business Logic = ends with "UseCase" (e.g., CreateTrainingUseCase)
 * - Repository Adapters = ends with "RepoAdapter" (e.g., CreateTrainingRepoAdapter)
 *
 * Note: Current "*DomainUseCase" classes should be renamed to "*UseCase"
 * and "*UseCase" classes (repository adapters) should become "*RepoAdapter"
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    // ========= DOMAIN USE CASES - Business Logic Layer =========

    @Provides
    @Singleton
    fun provideBreakManagementUseCase(repository: Repository): BreakManagementUseCase =
        BreakManagementUseCase(repository)

    // TODO: Rename to CreateTrainingUseCase (remove "Domain" suffix)
    @Provides
    @Singleton
    fun provideFetchTrainingDomainUseCase(repository: Repository): FetchTrainingDomainUseCase =
        FetchTrainingDomainUseCase(repository)

    // TODO: Rename to CreateTrainingUseCase (remove "Domain" suffix)
    @Provides
    @Singleton
    fun provideCreateTrainingDomainUseCase(repository: Repository): CreateTrainingDomainUseCase =
        CreateTrainingDomainUseCase(repository)

    // TODO: Rename to EditTrainingUseCase (remove "Domain" suffix)
    @Provides
    @Singleton
    fun provideEditTrainingDomainUseCase(repository: Repository): EditTrainingDomainUseCase =
        EditTrainingDomainUseCase(repository)
}
