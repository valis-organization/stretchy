package com.example.stretchy.features.traininglist.di

import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.datatransport.DataExporterImpl
import com.example.stretchy.features.datatransport.DataImporterImpl
import com.example.stretchy.features.domain.usecases.CopyTrainingUseCase
import com.example.stretchy.features.domain.usecases.DeleteTrainingUseCase
import com.example.stretchy.features.domain.usecases.FetchTrainingListUseCase
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class TrainingListModule {
    @Provides
    fun provideTrainingListViewModel(
        repository: Repository,
        dataImporterImpl: DataImporterImpl,
        dataExporterImpl: DataExporterImpl,
        trainingType: TrainingType
    ) =
        TrainingListViewModel(
            FetchTrainingListUseCase(repository),
            DeleteTrainingUseCase(repository),
            CopyTrainingUseCase(repository),
            dataImporterImpl,
            dataExporterImpl,
            trainingType
        )
}
