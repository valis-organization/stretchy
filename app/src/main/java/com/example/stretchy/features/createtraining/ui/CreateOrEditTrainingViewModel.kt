package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.extensions.toActivityType
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.AutomaticBreakPreferences
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateOrEditTrainingViewModel(
    val repository: Repository,
    val trainingId: Long,
    private val automaticBreakPreferences: AutomaticBreakPreferences,
    val trainingType: TrainingType
) :
    ViewModel() {
    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init)
    val uiState: StateFlow<CreateTrainingUiState> = _uiState

    init {
        if (trainingId != -1L) {
            viewModelScope.launch(Dispatchers.IO) {
                val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)

                with(trainingWithActivities) {
                    _uiState.emit(
                        CreateTrainingUiState.Success(
                            trainingId,
                            true,
                            name,
                            activities,
                            trainingType,
                            false,
                            isCreateTrainingButtonVisible(name),
                            isAutomaticBreakButtonClicked = true
                        )
                    )
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.emit(
                    CreateTrainingUiState.Success(
                        trainingId,
                        false,
                        "",
                        emptyList(),
                        trainingType,
                        false,
                        saveButtonCanBeClicked = false,
                        isAutomaticBreakButtonClicked = true
                    )
                )
            }
        }
    }

    fun editTraining(trainingId: Long, exerciseList: List<ExercisesWithBreaks>) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val activitiesList = exerciseList.mapToActivityList()
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.editTrainingWithActivities(
                    trainingId,
                    TrainingWithActivity(
                        stateSuccess.currentName,
                        trainingType,
                        true,
                        activitiesList
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
            }
        }
    }

    fun setTrainingName(trainingName: String) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        with(stateSuccess) {
            viewModelScope.launch {
                _uiState.value = copy(
                    currentName = trainingName,
                    saveButtonCanBeClicked = isCreateTrainingButtonVisible(
                        trainingName
                    ),
                    isTrainingChanged = isTrainingChanged(
                        trainingId,
                        trainingName,
                        activities
                    )
                )
            }
        }
    }

    fun createTraining(exerciseList: List<ExercisesWithBreaks>) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val activitiesList = exerciseList.mapToActivityList()
        viewModelScope.launch {
            if (stateSuccess.currentName == "") {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.MissingTrainingName))
            } else {
                try {
                    saveTraining(activitiesList)
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

    fun enableAutoBreaks() {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        _uiState.value = stateSuccess.copy(isAutomaticBreakButtonClicked = true)
    }

    fun disableAutoBreaks() {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        _uiState.value = stateSuccess.copy(isAutomaticBreakButtonClicked = false)
    }

    fun updateAutoBreakDuration(durationInSec: Int) =
        automaticBreakPreferences.updateAutoBreakDuration(durationInSec)

    fun getAutoBreakDuration(): Int = automaticBreakPreferences.getCurrentAutoBreakDuration()

    private fun saveTraining(activitiesList: List<Activity>) {
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.addTrainingWithActivities(
                    TrainingWithActivity(
                        success.currentName,
                        trainingType,
                        true,
                        activitiesList
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
            }
        }
    }

    private fun isCreateTrainingButtonVisible(
        currentName: String
    ) =
        currentName.isNotBlank()

    private suspend fun isTrainingChanged(
        trainingId: Long?,
        trainingName: String,
        activities: List<Activity>
    ): Boolean {
        if (trainingId != null && trainingId >= 0) {
            val trainingFromDb = repository.getTrainingWithActivitiesById(trainingId)
            if (trainingFromDb.name == trainingName) {
                if (trainingFromDb.activities.size == activities.size) {
                    trainingFromDb.activities.forEachIndexed { index, activity ->
                        if (activity != activities[index]) {
                            return true
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun List<ExercisesWithBreaks>.mapToActivityList(): List<Activity> {
        val activityList = mutableListOf<Activity>()
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success

        var activityOrder = 0

        this.forEach {
            with(it.exercise) {
                activityList.add(
                    Activity(
                        name,
                        activityOrder,
                        duration,
                        stateSuccess.trainingType.toActivityType(duration == 0)
                    )
                )
            }
            activityOrder++
            if (it.nextBreakDuration != 0 && it.nextBreakDuration != null) {
                activityList.add(
                    Activity(
                        "",
                        activityOrder,
                        it.nextBreakDuration!!,
                        ActivityType.BREAK
                    )
                )
                activityOrder++
            }
        }
        return activityList
    }
}


