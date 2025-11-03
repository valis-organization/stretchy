package com.example.stretchy.features.traininglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.datatransport.DataExporterImpl
import com.example.stretchy.features.datatransport.DataImporterImpl
import com.example.stretchy.features.domain.usecases.CopyTrainingUseCase
import com.example.stretchy.features.domain.usecases.DeleteTrainingUseCase
import com.example.stretchy.features.domain.usecases.FetchTrainingListUseCase
import com.example.stretchy.features.traininglist.domain.toTraining
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class TrainingListViewModel(
    private val fetchTrainingListUseCase: FetchTrainingListUseCase,
    private val deleteTrainingUseCase: DeleteTrainingUseCase,
    private val copyTrainingUseCase: CopyTrainingUseCase,
    private val dataImporterImpl: DataImporterImpl,
    private val dataExporterImpl: DataExporterImpl,
    private val trainingType: TrainingType
) : ViewModel() {
    private val _uiState = MutableStateFlow<TrainingListUiState>(TrainingListUiState.Empty)
    val uiState: StateFlow<TrainingListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchTrainingList()
        }
    }

    private suspend fun fetchTrainingList() {
        _uiState.value = TrainingListUiState.Loading
        val trainingWithActivityList = fetchTrainingListUseCase()
        if (trainingWithActivityList.isEmpty()) {
            _uiState.value = TrainingListUiState.Empty
        } else {
            val list: List<Training> = trainingWithActivityList.mapToTraining()
            if (list.isEmpty()) {
                _uiState.value = TrainingListUiState.Empty
            } else {
                _uiState.value =
                    TrainingListUiState.Loaded(list)
            }

        }
    }

    suspend fun import() {
        dataImporterImpl.importData()
        fetchTrainingList()
    }

    fun export() {
        viewModelScope.launch {
            dataExporterImpl.exportData()
        }
    }

    private fun List<TrainingWithActivity>.mapToTraining(): List<Training> {
        val list = mutableListOf<Training>()
        this.forEach {
            if (it.trainingType == trainingType) {
                list.add(it.toTraining())
            }
        }
        return list
    }

    fun deleteTraining(training: Training) {
        viewModelScope.launch {
            deleteTrainingUseCase(training.id.toLong())
            fetchTrainingList()
        }
    }

    fun copyTraining(training: Training) {
        viewModelScope.launch {
            copyTrainingUseCase(training.id.toLong())
            fetchTrainingList()
        }
    }

    companion object {
        const val COPY = " copy"
    }
}
