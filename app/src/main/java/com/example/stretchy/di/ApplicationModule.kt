package com.example.stretchy.di

import android.content.Context
import androidx.room.Room
import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.AppDatabase.Companion.MIGRATION_1_2
import com.example.stretchy.database.AppDatabase.Companion.MIGRATION_2_3
import com.example.stretchy.features.createtraining.ui.data.AutomaticBreakPreferences
import com.example.stretchy.features.datatransport.DataExporterImpl
import com.example.stretchy.features.datatransport.DataImporterImpl
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideRepository(appDatabase: AppDatabase): Repository {
        return RepositoryImpl(appDatabase)
    }

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, AppDatabase.NAME
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()

    @Provides
    @Singleton
    fun provideAutomaticBreakPreferences(@ApplicationContext context: Context): AutomaticBreakPreferences {
        return AutomaticBreakPreferences(context)
    }

    @Provides
    @Singleton
    fun provideSoundPlayer(@ApplicationContext context: Context): SoundPlayer {
        return SoundPlayer(context)
    }

    @Provides
    @Singleton
    fun provideDataImporter(repository: Repository): DataImporterImpl {
        return DataImporterImpl(repository)
    }

    @Provides
    @Singleton
    fun provideDataExporter(repository: Repository): DataExporterImpl {
        return DataExporterImpl(repository)
    }

    // ========= USE CASE PROVIDERS - Clean Architecture =========

    @Provides
    @Singleton
    fun provideFetchTrainingByIdUseCase(repository: Repository): com.example.stretchy.features.domain.usecases.FetchTrainingByIdUseCase =
        com.example.stretchy.features.domain.usecases.FetchTrainingByIdUseCase(repository)

    @Provides
    @Singleton
    fun provideCreateTrainingUseCase(repository: Repository): com.example.stretchy.features.domain.usecases.CreateTrainingUseCase =
        com.example.stretchy.features.domain.usecases.CreateTrainingUseCase(repository)

    @Provides
    @Singleton
    fun provideEditTrainingUseCase(repository: Repository): com.example.stretchy.features.domain.usecases.EditTrainingUseCase =
        com.example.stretchy.features.domain.usecases.EditTrainingUseCase(repository)

    @Provides
    @Singleton
    fun provideBreakManagementUseCase(repository: Repository): com.example.stretchy.features.createtraining.domain.BreakManagementUseCase =
        com.example.stretchy.features.createtraining.domain.BreakManagementUseCase(repository)

    @Provides
    @Singleton
    fun provideFetchTrainingDomainUseCase(repository: Repository): com.example.stretchy.features.domain.usecases.FetchTrainingDomainUseCase =
        com.example.stretchy.features.domain.usecases.FetchTrainingDomainUseCase(repository)

    @Provides
    @Singleton
    fun provideCreateTrainingDomainUseCase(repository: Repository): com.example.stretchy.features.domain.usecases.CreateTrainingDomainUseCase =
        com.example.stretchy.features.domain.usecases.CreateTrainingDomainUseCase(repository)

    @Provides
    @Singleton
    fun provideEditTrainingDomainUseCase(repository: Repository): com.example.stretchy.features.domain.usecases.EditTrainingDomainUseCase =
        com.example.stretchy.features.domain.usecases.EditTrainingDomainUseCase(repository)
}
