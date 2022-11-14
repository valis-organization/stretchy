package com.example.stretchy.features.executetraining.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.di.DaggerRepositoryComponent
import com.example.stretchy.repository.ActivityDomain
import com.example.stretchy.features.executetraining.Timer
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
import com.example.stretchy.di.RepositoryComponent
import com.example.stretchy.repository.RepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExecuteTrainingViewModel : ViewModel() {
    private var timer: Timer = Timer()
    private val _uiState = MutableStateFlow<ExecuteTrainingUiState>(ExecuteTrainingUiState.Loading)
    val uiState: StateFlow<ExecuteTrainingUiState> = _uiState

    @Inject
    lateinit var repository: RepositoryImpl
    private val repositoryComponent: RepositoryComponent = DaggerRepositoryComponent.create()
    private var isPaused = true

    init {
        repositoryComponent.inject(this)
        _uiState.value = ExecuteTrainingUiState.Loading
        viewModelScope.launch {
            val exercisesList = repository.getActivitiesForTraining("someId")
            if (exercisesList.isEmpty()) {
                _uiState.value = ExecuteTrainingUiState.Error
            } else {
                exercisesList.forEachIndexed { index, exercise ->
                    timer.setSeconds(exercise.duration)
                    timer.flow.takeWhile { it >= 0 }.collect { currentSeconds ->
                        when (exercise) {
                            is ActivityDomain.ExerciseDomain -> {
                                val nextExerciseName =
                                    (exercisesList.getOrNull(index + 2) as? ActivityDomain.ExerciseDomain)?.name
                                _uiState.value = ExecuteTrainingUiState.Success(
                                    ActivityItem.Exercise(
                                        exercise.name,
                                        nextExerciseName,
                                        currentSeconds,
                                        exercise.duration
                                    )
                                )
                            }
                            is ActivityDomain.BreakDomain -> {
                                val nextExerciseName =
                                    (exercisesList.getOrNull(index + 1) as? ActivityDomain.ExerciseDomain)?.name
                                _uiState.value = ExecuteTrainingUiState.Success(
                                    ActivityItem.Break(
                                        nextExerciseName!!,
                                        currentSeconds,
                                        exercise.duration
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun toggleStartStopTimer() {
        if (!isPaused) {
            isPaused = true
            Log.i(TIMER_LOG_TAG, "Timer is paused.")
            timer.pause()
        } else {
            isPaused = false
            Log.i(TIMER_LOG_TAG, "Timer is resumed.")
            timer.start()
        }
    }

    companion object {
        private const val TIMER_LOG_TAG = "TIMER"
        private const val BREAK = "Break"
    }
}