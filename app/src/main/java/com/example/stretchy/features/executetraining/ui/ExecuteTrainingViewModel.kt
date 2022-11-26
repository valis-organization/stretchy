package com.example.stretchy.features.executetraining.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.Timer
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
import com.example.stretchy.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class ExecuteTrainingViewModel(val repository: Repository) : ViewModel() {
    private var timer: Timer = Timer()
    private val _uiState = MutableStateFlow<ExecuteTrainingUiState>(ExecuteTrainingUiState.Loading)
    val uiState: StateFlow<ExecuteTrainingUiState> = _uiState

    private var isPaused = true

    fun init(trainingId: Long) {
        _uiState.value = ExecuteTrainingUiState.Loading
        viewModelScope.launch {

            val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
            if (trainingWithActivities.activities.isEmpty()) {
                _uiState.value = ExecuteTrainingUiState.Error
            } else {
                trainingWithActivities.activities.forEachIndexed { index, exercise ->
                    timer.setSeconds(exercise.duration)
                    timer.flow.takeWhile { it >= 0 }.collect { currentSeconds ->
                        when (exercise.activityType) {
                            ActivityType.STRETCH -> {
                                val nextExerciseName =
                                    trainingWithActivities.activities.getOrNull(index + 2)?.name
                                _uiState.value = ExecuteTrainingUiState.Success(
                                    ActivityItem.Exercise(
                                        exercise.name,
                                        nextExerciseName,
                                        currentSeconds,
                                        exercise.duration
                                    )
                                )
                            }
                            ActivityType.BREAK -> {
                                val nextExerciseName =
                                    trainingWithActivities.activities.getOrNull(index + 1)?.name
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