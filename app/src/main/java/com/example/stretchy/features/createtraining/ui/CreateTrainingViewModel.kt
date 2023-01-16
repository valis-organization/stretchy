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

class CreateTrainingViewModel(val repository: Repository, val trainingId: Long) : ViewModel() {
    private var name: String? = null
    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init(name.toString()))
    val uiState: StateFlow<CreateTrainingUiState> = _uiState


    private var type: Training.Type = Training.Type.STRETCHING
    private var trainingExercisesList = mutableListOf<Activity>()

    init {
        if (trainingId != -1L) {
            viewModelScope.launch {
                val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
                name = trainingWithActivities.name
                trainingExercisesList.addAll(trainingWithActivities.activities)
                val currentName = name
                _uiState.emit(
                    CreateTrainingUiState.Editing(
                        trainingId, currentName!!,
                        trainingExercisesList
                    )
                )
            }
        }
    }

    fun addActivity(activityItem: Activity) {
        trainingExercisesList.add(activityItem)
        val currentList = mutableListOf<Activity>()
        currentList.addAll(trainingExercisesList)
        viewModelScope.launch {
            _uiState.emit(
                CreateTrainingUiState.Success(
                    currentList
                )
            )
        }
    }

    fun editActivity(activityItem: Activity, listId: Int) {
        trainingExercisesList[listId] = activityItem
        val currentList = mutableListOf<Activity>()
        currentList.addAll(trainingExercisesList)
        viewModelScope.launch {
            _uiState.emit(
                CreateTrainingUiState.Success(
                    currentList
                )
            )
        }
    }

    fun setTrainingName(trainingName: String) {
        this.name = trainingName
    }

    fun createTraining() {
        viewModelScope.launch {
            if (name == null) {
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

    private fun currentActivitySizeList(): Int =
        (_uiState.value as? CreateTrainingUiState.Success)?.activities?.size ?: 0

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
                trainingExercisesList.clear()
                _uiState.emit(CreateTrainingUiState.Done)
            } else {
                //todo toast
            }
        }
    }

    fun editTraining(trainingId: Long) {
        val currentExercises = mutableListOf<Activity>()
        currentExercises.addAll(trainingExercisesList)
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.editTrainingWithActivities(
                    trainingId,
                    TrainingWithActivity(
                        name!!,
                        TrainingType.STRETCH,
                        true,
                        trainingExercisesList
                    )
                )
                trainingExercisesList.clear()
                _uiState.emit(CreateTrainingUiState.Done)
            }
        }
    }

    fun deleteExercise(exerciseListPosition: Int) {
        trainingExercisesList.removeAt(exerciseListPosition)
        val currentExercises = mutableListOf<Activity>()
        currentExercises.addAll(trainingExercisesList)

        viewModelScope.launch {
            _uiState.emit(
                CreateTrainingUiState.Success(
                    currentExercises
                )
            )
        }
    }

    fun swapExercises(from: Int, to: Int) {
        trainingExercisesList = trainingExercisesList.toMutableList().apply { add(to,removeAt(from)) }

        viewModelScope.launch {
            _uiState.emit(
                CreateTrainingUiState.Success(
                    trainingExercisesList
                )
            )
        }
    }
}