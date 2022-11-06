package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.StretchyDataBase
import com.example.stretchy.features.training.ui.data.ActivityItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrateTrainingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<TrainingUiState> = MutableStateFlow(TrainingUiState.Init)
    val uiState: StateFlow<TrainingUiState> = _uiState
    private val db = StretchyDataBase()
    private val activities: MutableList<ActivityItem> = mutableListOf()
    private var trainingName: String? = null

    fun addActivity(activityItem: ActivityItem) {
        activities.add(activityItem)

    }

    fun replaceActivities(firstPos: Int, secondPos: Int) {
        val firstToReplace = activities[firstPos]
        activities[firstPos] = activities[secondPos]
        activities[secondPos] = firstToReplace
        viewModelScope.launch {
            _uiState.emit(TrainingUiState.Success(activities))
        }
    }

    fun setTrainingName(trainingName: String) {
        this.trainingName = trainingName
    }

    fun createTraining() {
        val l = emptyList<String>()

        l.forEach {

        }
        viewModelScope.launch {
            if (trainingName == null) {
                _uiState.emit(TrainingUiState.Error(TrainingUiState.Error.Reason.MissingTrainingName))
            } else if (activities.size < 2) {
                _uiState.emit(TrainingUiState.Error(TrainingUiState.Error.Reason.NotEnoughExercises))
            } else {
                try {
                    db.addTraining(trainingName!!, activities)
                    _uiState.emit(TrainingUiState.Done)
                } catch (ex: Exception) {
                    _uiState.emit(TrainingUiState.Error(TrainingUiState.Error.Reason.Unknown(ex)))
                }
            }
        }
    }
}