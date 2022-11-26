package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateTrainingViewModel(database: Repository) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init)
    val uiState: StateFlow<CreateTrainingUiState> = _uiState
    private val activities: MutableList<ActivityItem> = mutableListOf()

    private var name: String? = null
    private var type: Training.Type = Training.Type.STRETCHING

    fun addActivity(activityItem: ActivityItem) {
        activities.add(activityItem)
    }

    fun setTrainingName(trainingName: String) {
        this.name = trainingName
    }

    fun createTraining() {
        viewModelScope.launch {
            if (name == null) {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.MissingTrainingName))
            } else if (activities.size < 2) {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.NotEnoughExercises))
            } else {
                try {
                    composeTraining()
                    saveTraining()
                    _uiState.emit(CreateTrainingUiState.Done)
                } catch (ex: Exception) {
                    _uiState.emit(
                        CreateTrainingUiState.Error(
                            CreateTrainingUiState.Error.Reason.Unknown(
                                ex
                            )
                        )
                    )
                }
            }
        }
    }

    private fun composeTraining() {
        if (type == Training.Type.STRETCHING) {
            activities.forEach {
                if (it is ActivityItem.Break) {
                    throw Exception("BUG!, It should not be possible to create Break by hand with Streching program")
                }
            }
        }
    }

    private fun saveTraining() {

    }

    fun replaceActivities(firstPos: Int, secondPos: Int) {
        val firstToReplace = activities[firstPos]
        activities[firstPos] = activities[secondPos]
        activities[secondPos] = firstToReplace
        viewModelScope.launch {
            _uiState.emit(CreateTrainingUiState.Success(activities))
        }
    }
}