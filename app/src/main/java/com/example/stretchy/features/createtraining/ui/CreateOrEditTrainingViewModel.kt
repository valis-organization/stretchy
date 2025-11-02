package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.domain.toActivityList
import com.example.stretchy.features.createtraining.domain.toExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.AutomaticBreakPreferences
import com.example.stretchy.features.domain.usecases.CreateTrainingUseCase
import com.example.stretchy.features.domain.usecases.EditTrainingUseCase
import com.example.stretchy.features.domain.usecases.FetchTrainingByIdUseCase
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateOrEditTrainingViewModel(
    private val fetchTrainingByIdUseCase: FetchTrainingByIdUseCase,
    private val createTrainingUseCase: CreateTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    val trainingId: Long,
    private val automaticBreakPreferences: AutomaticBreakPreferences,
    val trainingType: TrainingType
) : ViewModel() {

    // Secondary constructor retained for backward compatibility with existing DI modules
    constructor(
        repository: Repository,
        trainingId: Long,
        automaticBreakPreferences: AutomaticBreakPreferences,
        trainingType: TrainingType
    ) : this(
        FetchTrainingByIdUseCase(repository),
        CreateTrainingUseCase(repository),
        EditTrainingUseCase(repository),
        trainingId,
        automaticBreakPreferences,
        trainingType
    )

    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init)
    val uiState: StateFlow<CreateTrainingUiState> = _uiState.asStateFlow()

    init {
        if (trainingId != -1L) {
            viewModelScope.launch(Dispatchers.IO) {
                val trainingWithActivities = fetchTrainingByIdUseCase(trainingId)

                with(trainingWithActivities) {
                    val exerciseList = activities.toExercisesWithBreaks()
                    _uiState.emit(
                        CreateTrainingUiState.Success(
                            trainingId,
                            true,
                            name,
                            exerciseList,
                            trainingType,
                            false,
                            isCreateTrainingButtonVisible(name, exerciseList),
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
        val activitiesList = exerciseList.toActivityList(trainingType)
        viewModelScope.launch {
            val success = (_uiState.value as? CreateTrainingUiState.Success)
            if (success != null) {
                editTrainingUseCase(trainingId, TrainingWithActivity(
                    stateSuccess.currentName,
                    trainingType,
                    true,
                    activitiesList
                ))
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
                        trainingName,
                        exercisesWithBreaks
                    ),
                    isTrainingChanged = isTrainingChanged(
                        trainingId,
                        trainingName,
                        exercisesWithBreaks
                    )
                )
            }
        }
    }

    fun setExercises(exerciseList: List<ExercisesWithBreaks>) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        with(stateSuccess) {
            viewModelScope.launch {
                _uiState.value = copy(
                    exercisesWithBreaks = exerciseList,
                    saveButtonCanBeClicked = isCreateTrainingButtonVisible(
                        currentName,
                        exerciseList
                    ),
                    isTrainingChanged = isTrainingChanged(
                        trainingId,
                        currentName,
                        exercisesWithBreaks
                    )
                )
            }
        }
    }

    fun createTraining(exerciseList: List<ExercisesWithBreaks>) {
        val stateSuccess = _uiState.value as CreateTrainingUiState.Success
        val activitiesList = exerciseList.toActivityList(trainingType)
        viewModelScope.launch {
            if (stateSuccess.currentName == "") {
                _uiState.emit(CreateTrainingUiState.Error(CreateTrainingUiState.Error.Reason.MissingTrainingName))
            } else {
                try {
                    createTrainingUseCase(TrainingWithActivity(
                        stateSuccess.currentName,
                        trainingType,
                        true,
                        activitiesList
                    ))
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

    private fun isCreateTrainingButtonVisible(
        currentName: String,
        exerciseList: List<ExercisesWithBreaks>
    ) =
        currentName.isNotBlank() && exerciseList.size >= 2 && exerciseList[exerciseList.lastIndex].exercise.name.isNotBlank()

    private suspend fun isTrainingChanged(
        trainingId: Long?,
        trainingName: String,
        exerciseList: List<ExercisesWithBreaks>
    ): Boolean {
        if (trainingId != null && trainingId >= 0) {
            val trainingFromDb = fetchTrainingByIdUseCase(trainingId)
            if (trainingFromDb.name == trainingName) {
                if (trainingFromDb.activities.toExercisesWithBreaks() == exerciseList) {
                    return false
                }
            }
        }
        return true
    }
}
