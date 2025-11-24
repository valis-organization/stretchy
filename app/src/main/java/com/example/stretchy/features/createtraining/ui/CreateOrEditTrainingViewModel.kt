package com.example.stretchy.features.createtraining.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.domain.toActivityList
import com.example.stretchy.features.createtraining.domain.toExercisesWithBreaks
import com.example.stretchy.features.createtraining.domain.TrainingDomainMapper.toDomain
import com.example.stretchy.features.createtraining.domain.TrainingDomainMapper.toUI
import com.example.stretchy.features.createtraining.domain.BreakManagementUseCase
import com.example.stretchy.features.domain.usecases.FetchTrainingDomainUseCase
import com.example.stretchy.features.domain.usecases.CreateTrainingDomainUseCase
import com.example.stretchy.features.domain.usecases.EditTrainingDomainUseCase
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

    // Legacy use cases for backward compatibility
    private val fetchTrainingByIdUseCase = FetchTrainingByIdUseCase(repository)
    private val createTrainingUseCase = CreateTrainingUseCase(repository)
    private val editTrainingUseCase = EditTrainingUseCase(repository)

    // NEW: Domain layer use cases with clean architecture
    private val breakManagementUseCase = BreakManagementUseCase(repository)
    private val fetchTrainingDomainUseCase = FetchTrainingDomainUseCase(repository)
    private val createTrainingDomainUseCase = CreateTrainingDomainUseCase(repository)
    private val editTrainingDomainUseCase = EditTrainingDomainUseCase(repository)


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

    // ========= NEW DOMAIN-BASED METHODS - Clean Architecture =========

    /**
     * Updates break for specific exercise using domain logic
     * Demonstrates clean separation between UI and domain layers
     */
    fun updateExerciseBreak(exerciseIndex: Int, newBreakDuration: Int?) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? CreateTrainingUiState.Success ?: return@launch
                val currentExercises = currentState.exercisesWithBreaks.toMutableList()

                if (exerciseIndex !in currentExercises.indices) return@launch

                val currentExercise = currentExercises[exerciseIndex].toDomain()

                // Use domain use case for break management
                val updatedExercise = breakManagementUseCase.updateExerciseBreak(
                    currentExercise,
                    newBreakDuration
                )

                // Convert back to UI model
                currentExercises[exerciseIndex] = updatedExercise.toUI(exerciseIndex)

                // Update UI state
                _uiState.value = currentState.copy(
                    exercisesWithBreaks = currentExercises,
                    isTrainingChanged = true,
                    saveButtonCanBeClicked = isCreateTrainingButtonVisible(currentState.currentName, currentExercises)
                )

            } catch (e: Exception) {
                _events.emit(UiEvent.ShowErrorDialog("Failed to update break: ${e.message}"))
            }
        }
    }

    /**
     * Applies automatic break to exercise using domain rules
     */
    fun applyAutomaticBreakToExercise(exerciseIndex: Int) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? CreateTrainingUiState.Success ?: return@launch
                val currentExercises = currentState.exercisesWithBreaks.toMutableList()

                if (exerciseIndex !in currentExercises.indices) return@launch

                val exercise = currentExercises[exerciseIndex].toDomain().exercise
                val autoBreakDuration = getAutoBreakDuration()

                // Use domain use case for automatic break logic
                val exerciseWithBreak = breakManagementUseCase.applyAutomaticBreak(exercise, autoBreakDuration)

                // Convert back to UI model
                currentExercises[exerciseIndex] = exerciseWithBreak.toUI(exerciseIndex)

                _uiState.value = currentState.copy(
                    exercisesWithBreaks = currentExercises,
                    isTrainingChanged = true
                )

            } catch (e: Exception) {
                _events.emit(UiEvent.ShowErrorDialog("Failed to apply automatic break: ${e.message}"))
            }
        }
    }

    /**
     * Validates exercise with break using domain rules
     */
    fun validateExerciseWithBreak(exerciseIndex: Int): Boolean {
        val currentState = _uiState.value as? CreateTrainingUiState.Success ?: return false
        if (exerciseIndex !in currentState.exercisesWithBreaks.indices) return false

        val exerciseWithBreak = currentState.exercisesWithBreaks[exerciseIndex].toDomain()
        return breakManagementUseCase.isBreakCompatibleWithExercise(
            exerciseWithBreak.breakAfter,
            exerciseWithBreak.exercise
        )
    }

    /**
     * Gets break description using domain logic
     */
    fun getBreakDescription(exerciseIndex: Int): String {
        val currentState = _uiState.value as? CreateTrainingUiState.Success ?: return "No break"
        if (exerciseIndex !in currentState.exercisesWithBreaks.indices) return "No break"

        val breakDomain = currentState.exercisesWithBreaks[exerciseIndex].toDomain().breakAfter
        return breakDomain?.getDisplayText() ?: "No break"
    }

    /**
     * Creates training using domain validation (future replacement for legacy method)
     */
    fun createTrainingWithDomainValidation(exerciseList: List<ExercisesWithBreaks>) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? CreateTrainingUiState.Success ?: return@launch

                // Convert UI to domain with validation
                val trainingDomain = com.example.stretchy.features.createtraining.domain.TrainingDomainMapper.createDomainFromUI(
                    name = currentState.currentName,
                    exercisesWithBreaks = exerciseList,
                    trainingType = trainingType
                )

                // Validate using domain rules
                val validationErrors = com.example.stretchy.features.createtraining.domain.TrainingDomainRules.validateTraining(trainingDomain)
                if (validationErrors.isNotEmpty()) {
                    _events.emit(UiEvent.ShowErrorDialog("Validation failed: ${validationErrors.joinToString(", ")}"))
                    return@launch
                }

                // Create using domain use case
                createTrainingDomainUseCase(trainingDomain)
                _uiState.emit(CreateTrainingUiState.Done)

            } catch (e: Exception) {
                _events.emit(UiEvent.ShowErrorDialog("Failed to create training: ${e.message}"))
            }
        }
    }

    /**
     * Edits training using domain validation (future replacement for legacy method)
     */
    fun editTrainingWithDomainValidation(trainingId: Long, exerciseList: List<ExercisesWithBreaks>) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? CreateTrainingUiState.Success ?: return@launch

                // Convert UI to domain with validation
                val trainingDomain = com.example.stretchy.features.createtraining.domain.TrainingDomainMapper.createDomainFromUI(
                    name = currentState.currentName,
                    exercisesWithBreaks = exerciseList,
                    trainingType = trainingType
                ).copy(id = trainingId)

                // Validate using domain rules
                val validationErrors = com.example.stretchy.features.createtraining.domain.TrainingDomainRules.validateTraining(trainingDomain)
                if (validationErrors.isNotEmpty()) {
                    _events.emit(UiEvent.ShowErrorDialog("Validation failed: ${validationErrors.joinToString(", ")}"))
                    return@launch
                }

                // Edit using domain use case
                editTrainingDomainUseCase(trainingId, trainingDomain)
                _uiState.emit(CreateTrainingUiState.Done)

            } catch (e: Exception) {
                _events.emit(UiEvent.ShowErrorDialog("Failed to edit training: ${e.message}"))
            }
        }
    }
}
