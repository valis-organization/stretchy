package com.example.stretchy.features.traininglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingListViewModel(val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow<TrainingListUiState>(TrainingListUiState.Empty)
    val uiState: StateFlow<TrainingListUiState> = _uiState

    fun fetchTrainingList() {
        _uiState.value = TrainingListUiState.Loading
        viewModelScope.launch {
            val trainingWithActivityList = repository.getTrainingsWithActivities()
            if (trainingWithActivityList.isEmpty()) {
                _uiState.value = TrainingListUiState.Empty
            } else {
                _uiState.value =
                    TrainingListUiState.Loaded(trainingWithActivityList.map { it.toTraining() })
            }
        }
    }

    private fun TrainingWithActivity.toTraining(): Training {
        var duration = 0
        this.activities.forEach {
            duration += it.duration
        }
        return Training(
            this.id.toString(),
            this.name,
            this.activities.size,
            duration,
            this.trainingType.toTrainingType()
        )
    }

    private fun TrainingType.toTrainingType(): Training.Type {
        return when (this) {
            TrainingType.STRETCH -> Training.Type.STRETCHING
            TrainingType.BODYWEIGHT -> Training.Type.BODY_WEIGHT
        }
    }
}
