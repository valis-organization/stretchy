package com.example.stretchy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.dataBase.Repository
import com.example.stretchy.dataBase.StretchyDataBase
import com.example.stretchy.ui.theme.Exercise
import com.example.stretchy.ui.theme.ExercisesUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {
    private var countDownTimer: Timer = Timer()
    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val uiState: StateFlow<ExerciseUiState> = _uiState

    private val db = StretchyDataBase()
    private val repository = Repository(db)

    private var isPaused = true
    private val TIMER_LOG_TAG = "TIMER"

    init {
        _uiState.value = ExerciseUiState.Loading
        viewModelScope.launch {
            val exercisesList = repository.getExercisesList()
            if (exercisesList.isEmpty()) {
                _uiState.value = ExerciseUiState.Error
            } else {
                for ((counter, exercise) in exercisesList.withIndex()) {
                    countDownTimer.setSeconds(exercise.exerciseTimeLength)
                    val currentExercise = exercise.exerciseName
                    val nextExercise = exercisesList[counter + 1].exerciseName
                    countDownTimer.flow.takeWhile { it >= 0 }.collect {
                        val currentSecond = it
                        Log.i(TIMER_LOG_TAG, "$currentSecond")
                        if (currentExercise == "Break") {
                            _uiState.value = ExerciseUiState.Success(
                                ExercisesUiModel(
                                    Exercise(
                                        currentExercise,
                                        nextExercise,
                                        currentSecond,
                                        exercise.exerciseTimeLength
                                    )
                                )
                            )
                        } else {
                            _uiState.value = ExerciseUiState.Success(
                                ExercisesUiModel(
                                    Exercise(
                                        currentExercise,
                                        nextExercise,
                                        currentSecond,
                                        exercise.exerciseTimeLength
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun stopTimer() {
        if (!isPaused) {
            isPaused = true
            Log.i(TIMER_LOG_TAG, "Timer is paused.")
            countDownTimer.pause()
        } else {
            isPaused = false
            Log.i(TIMER_LOG_TAG, "Timer is resumed.")
            countDownTimer.start()
        }
    }

    fun resumeTimer() {
        countDownTimer.start()
    }

    sealed class ExerciseUiState {
        object Loading : ExerciseUiState()
        object Error : ExerciseUiState()
        class Success(val data: ExercisesUiModel) : ExerciseUiState()
    }
}