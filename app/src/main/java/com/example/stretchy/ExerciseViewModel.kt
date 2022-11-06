package com.example.stretchy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.dataBase.ActivityRepo
import com.example.stretchy.dataBase.Repository
import com.example.stretchy.dataBase.StretchyDataBase
import com.example.stretchy.ui.theme.ActivityItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {
    private var timer: Timer = Timer()
    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val uiState: StateFlow<ExerciseUiState> = _uiState

    private val db = StretchyDataBase()
    private val repository = Repository(db)
    private var isPaused = true

    init {
        _uiState.value = ExerciseUiState.Loading
        viewModelScope.launch {
            val exercisesList = repository.getActivities()
            if (exercisesList.isEmpty()) {
                _uiState.value = ExerciseUiState.Error
            } else {
                exercisesList.forEachIndexed { index, exercise ->
                    timer.setSeconds(exercise.duration)
                    timer.flow.takeWhile { it >= 0 }.collect { currentSeconds ->
                        when (exercise) {
                            is ActivityRepo.ExerciseRepo -> {
                                val nextExerciseName =
                                    (exercisesList.getOrNull(index + 2) as? ActivityRepo.ExerciseRepo)?.name
                                _uiState.value = ExerciseUiState.Success(
                                    ActivityItem.Exercise(
                                        exercise.name,
                                        nextExerciseName,
                                        currentSeconds,
                                        exercise.duration
                                    )
                                )
                            }
                            is ActivityRepo.BreakRepo -> {
                                val nextExerciseName =
                                    (exercisesList.getOrNull(index + 1) as? ActivityRepo.ExerciseRepo)?.name
                                _uiState.value = ExerciseUiState.Success(
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

    fun toggleStartOrStopTimer() {
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