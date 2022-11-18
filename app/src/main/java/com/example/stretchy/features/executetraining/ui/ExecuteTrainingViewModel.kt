package com.example.stretchy.features.executetraining.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.MockedDataBaseImpl
import com.example.stretchy.repository.ActivityDomain
import com.example.stretchy.features.executetraining.Timer
import com.example.stretchy.features.executetraining.ui.data.ActivityItem
import com.example.stretchy.features.executetraining.ui.data.ExecuteTrainingUiState
import com.example.stretchy.repository.RepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class ExecuteTrainingViewModel : ViewModel() {
    private var timer: Timer = Timer()
    private val _uiState = MutableStateFlow<ExecuteTrainingUiState>(ExecuteTrainingUiState.Loading)
    val uiState: StateFlow<ExecuteTrainingUiState> = _uiState

    private val db = MockedDataBaseImpl()
    private val repository = RepositoryImpl(db)
    private var isPaused = true

    init {
        _uiState.value = ExecuteTrainingUiState.Loading
        viewModelScope.launch {
            val activitiesList = repository.getActivitiesForTraining("someId")
            var trainingProgressPercent = 0f
            val totalNumberOfExercises = getTotalExercisesNumber(activitiesList)
            if (activitiesList.isEmpty()) {
                _uiState.value = ExecuteTrainingUiState.Error
            } else {
                activitiesList.forEachIndexed { index, activity ->
                    timer.setSeconds(activity.duration)
                    timer.flow.takeWhile { it >= 0 }.collect { currentSeconds ->
                        when (activity) {
                            is ActivityDomain.ExerciseDomain -> {
                                val nextExerciseName =
                                    (activitiesList.getOrNull(index + 2) as? ActivityDomain.ExerciseDomain)?.name
                                _uiState.value = ExecuteTrainingUiState.Success(
                                    ActivityItem.Exercise(
                                        activity.name,
                                        nextExerciseName,
                                        currentSeconds,
                                        activity.duration,
                                        trainingProgressPercent.toInt()
                                    )
                                )
                            }
                            is ActivityDomain.BreakDomain -> {
                                val nextExerciseName =
                                    (activitiesList.getOrNull(index + 1) as? ActivityDomain.ExerciseDomain)?.name
                                _uiState.value = ExecuteTrainingUiState.Success(
                                    ActivityItem.Break(
                                        nextExerciseName!!,
                                        currentSeconds,
                                        activity.duration,
                                        trainingProgressPercent.toInt()
                                    )
                                )
                            }
                        }
                    }
                    if (activity is ActivityDomain.ExerciseDomain) {
                        trainingProgressPercent+= 1/totalNumberOfExercises * 100
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
    private fun getTotalExercisesNumber(exerciseList: List<ActivityDomain>): Float {
        var activities = 0f
        exerciseList.forEach { activity ->
            if (activity is ActivityDomain.ExerciseDomain) {
                activities++
            }
        }
        return activities
    }
    companion object {
        private const val TIMER_LOG_TAG = "TIMER"
    }
}