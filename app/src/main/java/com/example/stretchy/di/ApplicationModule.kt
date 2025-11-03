package com.example.stretchy.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.AppDatabase.Companion.MIGRATION_1_2
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
    ).addMigrations(MIGRATION_1_2).build()

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
}
