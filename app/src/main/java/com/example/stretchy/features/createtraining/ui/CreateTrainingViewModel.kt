package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateTrainingViewModel(val repository: Repository) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init)
    val uiState: StateFlow<CreateTrainingUiState> = _uiState

    private var name: String? = null
    private val trainingExercisesList = mutableListOf<Activity>()

    fun addActivity(activityItem: Activity) {
        trainingExercisesList.add(activityItem)
        val currentList = mutableListOf<Activity>()
        currentList.addAll(trainingExercisesList)
        viewModelScope.launch {
            _uiState.emit(
                CreateTrainingUiState.Success(
                    currentList,
                    isCreateTrainingButtonVisible()
                )
            )
        }
    }

    private fun isCreateTrainingButtonVisible() =
        !name.isNullOrBlank() && trainingExercisesList.size >= 2

    fun setTrainingName(trainingName: String) {
        this.name = trainingName
        viewModelScope.launch {
            _uiState.emit(
                CreateTrainingUiState.TitleChanged(
                    trainingExercisesList,
                    isCreateTrainingButtonVisible()
                )
            )
        }
    }

    fun createTraining() {
        viewModelScope.launch {
            if (name == null) {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.MissingTrainingName))
            } else if (trainingExercisesList.size < 2) {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.NotEnoughExercises))
            } else {
                try {
                    saveTraining()
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

    private fun saveTraining() {
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.addTrainingWithActivities(
                    TrainingWithActivity(
                        name!!,
                        TrainingType.STRETCH,
                        true,
                        trainingExercisesList
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
                trainingExercisesList.clear()
            }
        }
    }
}