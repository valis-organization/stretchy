package com.example.stretchy.features.executetraining.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.common.convertSecondsToMinutes
import com.example.stretchy.repository.ActivityDomain
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.Timer
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.*

class ExecuteTrainingViewModel(val repository: Repository) : ViewModel() {
    private var timer: Timer = Timer()
    private val _uiState = MutableStateFlow<ExecuteTrainingUiState>(ExecuteTrainingUiState.Loading)
    private var startDateSaved = false
    val uiState: StateFlow<ExecuteTrainingUiState> = _uiState

    private var isPaused = true
    private var startDateMs = 0L

    fun init(trainingId: Long) {
        _uiState.value = ExecuteTrainingUiState.Loading
        viewModelScope.launch {
            val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
            var trainingProgressPercent = 0f
            val totalNumberOfExercises = getTotalExercisesNumber(trainingWithActivities)
            if (trainingWithActivities.activities.isEmpty()) {
                _uiState.value = ExecuteTrainingUiState.Error
            } else {
                trainingWithActivities.activities.forEachIndexed { index, exercise ->
                    timer.setSeconds(exercise.duration)
                    timer.flow.takeWhile { it >= 0 }.collect { currentSeconds ->
                        if (exercise == trainingWithActivities.activities[0] && !isPaused && !startDateSaved) {
                            val startDate = Calendar.getInstance()
                            Log.i("Start date", "Started on ${startDate.time}")
                            startDateMs = startDate.timeInMillis
                            startDateSaved = true
                        }

                        when (exercise.activityType) {
                            ActivityType.STRETCH -> {
                                val nextExerciseName =
                                    trainingWithActivities.activities.getOrNull(index + 2)?.name
                                _uiState.value = ExecuteTrainingUiState.Success(
                                    ActivityItem.Exercise(
                                        exercise.name,
                                        nextExerciseName,
                                        currentSeconds,
                                        exercise.duration,
                                        trainingProgressPercent.toInt()
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
                                        exercise.duration,
                                        trainingProgressPercent.toInt()
                                    )
                                )
                            }
                        }
                    }
                    if (exercise.activityType == ActivityType.STRETCH) {
                        trainingProgressPercent += 1 / totalNumberOfExercises * 100
                    }
                    if (trainingWithActivities.activities.getOrNull(index + 1) == null) {
                        val currentTime = Calendar.getInstance()
                        val seconds = (currentTime.timeInMillis - startDateMs) / 1000
                        _uiState.value =
                            ExecuteTrainingUiState.TrainingCompleted(
                                timeSpent = convertSecondsToMinutes(
                                    seconds
                                )
                            )
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

    private fun getTotalExercisesNumber(training: TrainingWithActivity): Float {
        var activities = 0f
        training.activities.forEach { activity ->
            if (activity.activityType == ActivityType.STRETCH) {
                activities++
            }
        }
        return activities
    }

    companion object {
        private const val TIMER_LOG_TAG = "TIMER"
        private const val BREAK = "Break"
    }
}