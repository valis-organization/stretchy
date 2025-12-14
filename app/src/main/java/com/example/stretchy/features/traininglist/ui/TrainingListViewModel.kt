package com.example.stretchy.features.traininglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.datatransport.DataExporter
import com.example.stretchy.features.datatransport.DataImporter
import com.example.stretchy.features.domain.usecases.CopyTrainingRepoAdapter
import com.example.stretchy.features.domain.usecases.DeleteTrainingRepoAdapter
import com.example.stretchy.features.domain.usecases.FetchTrainingListRepoAdapter
import com.example.stretchy.features.traininglist.domain.toTraining
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.repository.TrainingWithActivity
import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    private val fetchTrainingListRepoAdapter: FetchTrainingListRepoAdapter,
    private val deleteTrainingRepoAdapter: DeleteTrainingRepoAdapter,
    private val copyTrainingRepoAdapter: CopyTrainingRepoAdapter,
    private val dataImporter: DataImporter,
    private val dataExporter: DataExporter,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    // Get trainingType from savedStateHandle or default
    private var trainingType: TrainingType = savedStateHandle.get<TrainingType>("trainingType") ?: TrainingType.STRETCH

    // Method to set trainingType if needed
    fun setTrainingType(type: TrainingType) {
        if (trainingType != type) {
            trainingType = type
            savedStateHandle["trainingType"] = type
            // Reload data with new training type
            loadTrainings()
        }
    }
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

    fun loadTrainings() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchTrainingList()
        }
    }

    private suspend fun fetchTrainingList() {
        _uiState.value = TrainingListUiState.Loading
        try {
            val trainingWithActivityList = fetchTrainingListRepoAdapter()
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
            dataImporter.importData()
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
                dataExporter.exportData()
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
                deleteTrainingRepoAdapter(training.id.toLong())
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
                copyTrainingRepoAdapter(training.id.toLong())
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


}
