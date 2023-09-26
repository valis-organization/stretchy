package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType
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
    val automaticBreakPreferences: AutomaticBreakPreferences,
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
                            isCreateTrainingButtonVisible(name, activities),
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


    fun addBreakAfterActivity(activityItem: Activity) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        currentList.forEach { activity ->
            if (activity.activityOrder!! >= activityItem.activityOrder!!) {
                activity.activityOrder = activity.activityOrder!! + 1
            }
        }
        currentList.add(activityItem)

        viewModelScope.launch {
            emitActivitiesList(stateSuccess, currentList)
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
                        trainingType,
                        true,
                        stateSuccess.activities
                    )
                )
                _uiState.emit(CreateTrainingUiState.Done)
            }
        }
    }

    fun removeLocalActivity(activityOrder: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        val activityToRemove = currentList.find { it.activityOrder == activityOrder }
        val activityToRemoveIndex = currentList.indexOf(activityToRemove)
        if (currentList[activityToRemoveIndex + 1].activityType == ActivityType.BREAK) {
            val breakToRemove = currentList[activityToRemoveIndex + 1]
            currentList.remove(breakToRemove)
        }
        currentList.remove(activityToRemove)
        currentList.forEachIndexed { index, activity -> activity.activityOrder = index }

        viewModelScope.launch {
            emitActivitiesList(stateSuccess, currentList)
        }
    }

    fun addActivity(activityItem: Activity) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        val activityOrder = currentList.size
        currentList.add(activityItem.copy(activityOrder = activityOrder))

        viewModelScope.launch {
            emitActivitiesList(stateSuccess, currentList)
        }
    }

    fun addActivityWithBreak(exercise: Activity, nextBreak: Activity) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        val activityOrder = currentList.size
        currentList.add(exercise.copy(activityOrder = activityOrder))
        currentList.add(nextBreak.copy(activityOrder = activityOrder + 1))
        viewModelScope.launch {
            emitActivitiesList(stateSuccess, currentList)
        }
    }

    fun editActivity(activityItem: Activity) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val currentList = getCurrentActivities(stateSuccess)
        var activityToUpdate = currentList.find { it.activityOrder == activityItem.activityOrder }
        val activityToUpdateIndex = currentList.indexOf(activityToUpdate)
        with(activityItem) {
            activityToUpdate = activityItem.copy(name = name, duration = duration)
        }
        currentList[activityToUpdateIndex] = activityToUpdate!!
        viewModelScope.launch {
            emitActivitiesList(stateSuccess, currentList)
        }
    }

    fun setTrainingName(trainingName: String) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        with(stateSuccess) {
            viewModelScope.launch {
                _uiState.value = copy(
                    currentName = trainingName,
                    saveButtonCanBeClicked = isCreateTrainingButtonVisible(
                        trainingName,
                        activities
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

    fun swapExercises(fromPosition: Int, toPosition: Int) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val activities = getCurrentActivities(stateSuccess)
        activities[fromPosition + 1].activityOrder = toPosition
        activities[toPosition].activityOrder = fromPosition + 1
        activities[fromPosition + 2].activityOrder = toPosition + 1
        activities[toPosition + 1].activityOrder = fromPosition + 2

        _uiState.value = stateSuccess.copy(activities = activities.sortedBy { it.activityOrder })
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

    private fun currentActivitySizeList(): Int =
        (_uiState.value as? CreateTrainingUiState.Success)?.activities?.size ?: 0

    private fun saveTraining() {
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                repository.addTrainingWithActivities(
                    TrainingWithActivity(
                        success.currentName,
                        trainingType,
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

    private suspend fun emitActivitiesList(
        stateSuccess: CreateTrainingUiState.Success,
        currentList: List<Activity>
    ) {
        with(stateSuccess) {
            _uiState.value = copy(
                activities = currentList,
                saveButtonCanBeClicked = isCreateTrainingButtonVisible(
                    currentName,
                    currentList
                ),
                isTrainingChanged = isTrainingChanged(
                    trainingId,
                    currentName,
                    currentList
                )
            )
        }
    }

    private fun getAutoBreak(breakActivityOrder: Int): Activity {
        return Activity(
            "",
            activityOrder = breakActivityOrder,
            automaticBreakPreferences.getCurrentAutoBreakDuration(),
            ActivityType.BREAK
        )
    }
}