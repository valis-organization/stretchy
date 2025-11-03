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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowErrorDialog(val message: String) : UiEvent()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchTrainingList()
        }
    }

    private suspend fun fetchTrainingList() {
        _uiState.value = TrainingListUiState.Loading
        try {
            val trainingWithActivityList = fetchTrainingListUseCase()
            if (trainingWithActivityList.isEmpty()) {
                _uiState.value = TrainingListUiState.Empty
            } else {
                val list: List<Training> = trainingWithActivityList.mapToTraining()
                if (list.isEmpty()) {
                    _uiState.value = TrainingListUiState.Empty
                } else {
                    _uiState.value = TrainingListUiState.Loaded(list)
                }
            }
        } catch (throwable: Throwable) {
            _uiState.value = TrainingListUiState.Error(
                message = throwable.localizedMessage ?: "Failed to load trainings",
                throwable = throwable
            )
        }
    }

    suspend fun import() {
        try {
            dataImporterImpl.importData()
            fetchTrainingList()
        } catch (throwable: Throwable) {
            _uiState.value = TrainingListUiState.Error(
                message = throwable.localizedMessage ?: "Failed to import data",
                throwable = throwable
            )
        }
    }

    fun export() {
        viewModelScope.launch {
            try {
                dataExporterImpl.exportData()
                _events.emit(UiEvent.ShowToast("Data exported successfully"))
            } catch (throwable: Throwable) {
                _events.emit(UiEvent.ShowErrorDialog(throwable.localizedMessage ?: "Failed to export data"))
            }
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
            try {
                deleteTrainingUseCase(training.id.toLong())
                fetchTrainingList()
                _events.emit(UiEvent.ShowToast("Training '${training.name}' deleted"))
            } catch (throwable: Throwable) {
                _uiState.value = TrainingListUiState.Error(
                    message = throwable.localizedMessage ?: "Failed to delete training",
                    throwable = throwable
                )
            }
        }
    }

    fun copyTraining(training: Training) {
        viewModelScope.launch {
            try {
                copyTrainingUseCase(training.id.toLong())
                fetchTrainingList()
                _events.emit(UiEvent.ShowToast("Training '${training.name}' copied"))
            } catch (throwable: Throwable) {
                _uiState.value = TrainingListUiState.Error(
                    message = throwable.localizedMessage ?: "Failed to copy training",
                    throwable = throwable
                )
            }
        }
    }

    companion object {
        const val COPY = " copy"
    }
}
