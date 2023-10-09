package com.example.stretchy.features.traininglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.datatransport.DataExporterImpl
import com.example.stretchy.features.datatransport.DataImporterImpl
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingListViewModel(
    val repository: Repository,
    private val dataImporterImpl: DataImporterImpl,
    private val dataExporterImpl: DataExporterImpl,
    private val trainingType: TrainingType
) : ViewModel() {
    private val _uiState = MutableStateFlow<TrainingListUiState>(TrainingListUiState.Empty)
    val uiState: StateFlow<TrainingListUiState> = _uiState

    init {
        fetchTrainingList()
    }

    private fun fetchTrainingList() {
        _uiState.value = TrainingListUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val trainingWithActivityList = repository.getTrainingsWithActivities()
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
    }

    suspend fun import() {
        viewModelScope.launch {
            dataImporterImpl.importData()
        }
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

    private fun TrainingWithActivity.toTraining(): Training {
        var duration = 0
        this.activities.forEach { activity ->
            duration += activity.duration
        }
        return Training(
            this.id.toString(),
            this.name,
            this.activities.getExercisesSize(),
            duration,
            this.trainingType.toTrainingType()
        )
    }

    private fun List<Activity>.getExercisesSize(): Int {
        var size = 0
        this.forEach {
            if (it.activityType != ActivityType.BREAK) {
                size++
            }
        }
        return size
    }

    private fun TrainingType.toTrainingType(): Training.Type {
        return when (this) {
            TrainingType.STRETCH -> Training.Type.STRETCH
            TrainingType.BODYWEIGHT -> Training.Type.BODY_WEIGHT
        }
    }

    fun deleteTraining(training: Training) {
        viewModelScope.launch { repository.deleteTrainingById(training.id.toLong()) }
        fetchTrainingList()
    }

    fun copyTraining(training: Training) {
        viewModelScope.launch {
            with(training) {
                repository.getTrainingWithActivitiesById(id.toLong()).activities.let {
                    repository.addTrainingWithActivities(
                        TrainingWithActivity(
                            name + COPY,
                            TrainingType.STRETCH,
                            true,
                            it
                        )
                    )
                }
            }
        }
        fetchTrainingList()
    }

    companion object {
        const val COPY = " copy"
    }
}
