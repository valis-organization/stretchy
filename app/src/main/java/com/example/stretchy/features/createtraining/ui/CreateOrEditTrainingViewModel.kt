package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateOrEditTrainingViewModel(val repository: Repository, val trainingId: Long) :
    ViewModel() {
    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init)
    val uiState: StateFlow<CreateTrainingUiState> = _uiState

    init {
        if (trainingId != -1L) {
            viewModelScope.launch {
                val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
                with(trainingWithActivities) {
                    _uiState.emit(
                        CreateTrainingUiState.Success(
                            trainingId,
                            true,
                            name,
                            activities,
                            isCreateTrainingButtonVisible(name, activities)
                        )
                    )
                }
            }
        } else {
            viewModelScope.launch {
                _uiState.emit(
                    CreateTrainingUiState.Success(
                        trainingId,
                        false,
                        "",
                        emptyList(),
                        false
                    )
                )
            }
        }
    }

    fun editTraining(trainingId: Long) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.editTrainingWithActivities(
                    trainingId,
                    TrainingWithActivity(
                        stateSuccess.currentName,
                        TrainingType.STRETCH,
                        true,
                        stateSuccess.activities
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
            }
        }
    }

    fun removeLocalActivity(exerciseListPosition: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        currentList.removeAt(exerciseListPosition)
        _uiState.value = stateSuccess.copy(activities = currentList, createTrainingButtonVisible = isCreateTrainingButtonVisible(stateSuccess.currentName,currentList))
    }

    fun addActivity(activityItem: Activity) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        currentList.add(activityItem)
        _uiState.value = stateSuccess.copy(activities = currentList, createTrainingButtonVisible = isCreateTrainingButtonVisible(stateSuccess.currentName,currentList))
    }

    fun editActivity(activityItem: Activity, listId: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        currentList[listId] = activityItem
        _uiState.value = stateSuccess.copy(activities = currentList, createTrainingButtonVisible = isCreateTrainingButtonVisible(stateSuccess.currentName,currentList))
    }

    fun setTrainingName(trainingName: String) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        _uiState.value = stateSuccess.copy(currentName = trainingName, createTrainingButtonVisible = isCreateTrainingButtonVisible(trainingName,stateSuccess.activities))
    }

    fun createTraining() {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        viewModelScope.launch {
            if (stateSuccess.currentName == "") {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.MissingTrainingName))
            } else if (currentActivitySizeList() < 2) {
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

    fun swapExercises(from: Int, to: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val activities = getCurrentActivities(stateSuccess)
        activities.apply { add(to, removeAt(from)) }
        _uiState.value = stateSuccess.copy(activities = activities)
    }

    private fun currentActivitySizeList(): Int =
        (_uiState.value as? CreateTrainingUiState.Success)?.activities?.size ?: 0

    private fun saveTraining() {
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.addTrainingWithActivities(
                    TrainingWithActivity(
                        success.currentName,
                        TrainingType.STRETCH,
                        true,
                        success.activities
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
            } else {
                //todo toast
            }
        }
    }

    private fun getCurrentActivities(state: CreateTrainingUiState.Success): MutableList<Activity> {
        val currentList = mutableListOf<Activity>()
        currentList.addAll(state.activities)
        return currentList
    }

    private fun isCreateTrainingButtonVisible(
        currentName: String,
        currentExercises: List<Activity>
    ) =
        currentName.isNotBlank() && currentExercises.size >= 2
}