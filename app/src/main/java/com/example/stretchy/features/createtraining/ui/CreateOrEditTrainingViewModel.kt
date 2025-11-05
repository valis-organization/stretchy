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
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CreateOrEditTrainingViewModel @Inject constructor(
    repository: Repository,
    private val automaticBreakPreferences: AutomaticBreakPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get parameters from savedStateHandle
    val trainingId: Long = savedStateHandle.get<String>("id")?.toLongOrNull() ?: -1L
    val trainingType: TrainingType = savedStateHandle.get<String>("trainingType")?.let {
        try {
            TrainingType.valueOf(it)
        } catch (_: IllegalArgumentException) {
            TrainingType.STRETCH
        }
    } ?: TrainingType.STRETCH

    private val fetchTrainingByIdUseCase = FetchTrainingByIdUseCase(repository)
    private val createTrainingUseCase = CreateTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)


    private val _uiState: MutableStateFlow<CreateTrainingUiState> =
        MutableStateFlow(CreateTrainingUiState.Init)
    val uiState: StateFlow<CreateTrainingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowErrorDialog(val message: String) : UiEvent()
    }

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
