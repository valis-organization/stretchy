package com.example.stretchy.features.executetraining.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.common.convertSecondsToMinutes
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.executetraining.Timer
import com.example.stretchy.features.executetraining.ui.data.*
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.*

class ExecuteTrainingViewModel(val repository: Repository, val trainingId: Long) : ViewModel() {
    private var timer: Timer = Timer()
    private val _uiState = MutableStateFlow(ExecuteTrainingUiState(true, null, null, null, null, null))
    private var startingTimestampSaved = false
    val uiState: StateFlow<ExecuteTrainingUiState> = _uiState

    private var isPaused = true
    private var startingTimestamp = 0L

    var currentExerciseLoaded: Long? = null

    init {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {

            val trainingWithActivities = repository.getTrainingWithActivitiesById(trainingId)
            var trainingProgressPercent = 0f
            if (trainingWithActivities.activities.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = java.lang.Exception("empty activity list")
                )
            } else {
                val exercisesWithBreaks = getExercisesWithBreak(trainingWithActivities.activities)
                exercisesWithBreaks.forEachIndexed { index, activity ->
                    timer.setSeconds(activity.duration)
                    timer.flow.takeWhile { it >= 0 }.collect { currentSeconds ->
                        if (!startingTimestampSaved) {
                            saveStartingTimeStamp()
                        }
                        when (activity.activityType) {
                            ActivityType.STRETCH -> {
                                val nextExerciseName =
                                    exercisesWithBreaks.getOrNull(index + 2)?.name

                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = null,
                                    success = ActivityItem.Exercise(
                                        activity.name,
                                        nextExerciseName,
                                        currentSeconds,
                                        activity.duration,
                                        trainingProgressPercent
                                    ),
                                    readExerciseNameEvent = if (index == 0) {
                                        postReadExerciseNameEvent(
                                            activity.activityId,
                                            activity.name
                                        )
                                    } else {
                                        _uiState.value.readExerciseNameEvent
                                    }
                                )
                            }
                            ActivityType.BREAK -> {
                                val nextExerciseName =
                                    exercisesWithBreaks.getOrNull(index + 1)?.name

                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = null,
                                    success = ActivityItem.Break(
                                        nextExerciseName!!,
                                        currentSeconds,
                                        activity.duration,
                                        trainingProgressPercent
                                    ),
                                    readExerciseNameEvent = postReadExerciseNameEvent(
                                        activity.activityId,
                                        activity.name
                                    )
                                )
                            }
                            else -> {}
                        }
                    }
                    if (activity.activityType == ActivityType.STRETCH) {
                        trainingProgressPercent += 1 / trainingWithActivities.activities.size.toFloat()
                    }
                    if (trainingWithActivities.activities.getOrNull(index + 1) == null) {
                        val currentTime = Calendar.getInstance()
                        val seconds = (currentTime.timeInMillis - startingTimestamp) / 1000
                        _uiState.value = ExecuteTrainingUiState(
                            false, null, null, null,
                            TrainingCompleted(
                                currentTrainingTime = convertSecondsToMinutes(
                                    seconds
                                ),
                                numberOfExercises = trainingWithActivities.activities.size
                            ),
                            TrainingCompletedEvent()
                        )
                    }
                }
            }
        }
    }

    private fun postReadExerciseNameEvent(
        activityId: Long,
        name: String
    ): ReadExerciseNameEvent? {
        return if (currentExerciseLoaded != activityId) {
            currentExerciseLoaded = activityId
            ReadExerciseNameEvent(name)
        } else {
            _uiState.value.readExerciseNameEvent
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

    private fun getExercisesWithBreak(training: List<Activity>): List<Activity> {
        val exercisesWithBreaks: MutableList<Activity> = mutableListOf()
        training.forEachIndexed { i, exercise ->
            exercisesWithBreaks.add(exercise)
            if (i != training.lastIndex) {
                val nextExercise = training[i + 1]
                exercisesWithBreaks.add(Activity(nextExercise.name, 5, ActivityType.BREAK).apply {
                    activityId = nextExercise.activityId
                })
            }
        }
        return exercisesWithBreaks
    }

    private fun saveStartingTimeStamp() {
        val startDate = Calendar.getInstance()
        Log.i("Start date", "Started on ${startDate.time}")
        startingTimestamp = startDate.timeInMillis
        startingTimestampSaved = true
    }

    companion object {
        private const val TIMER_LOG_TAG = "TIMER"
    }
}