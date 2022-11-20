package com.example.stretchy.features.executetraining.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.dao.ActivityDao
import com.example.stretchy.database.dao.TrainingDao
import com.example.stretchy.database.dao.TrainingWithActivitiesDao
import com.example.stretchy.database.data.ActivityType
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

  /*  val db: AppDatabase = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "stretchydb"
    ).build()*/

    //todo inject repository to
    val db : AppDatabase = object : AppDatabase() {
      override fun activityDao(): ActivityDao {
          TODO("Not yet implemented")
      }

      override fun trainingDao(): TrainingDao {
          TODO("Not yet implemented")
      }

      override fun trainingWithActivitiesDao(): TrainingWithActivitiesDao {
          TODO("Not yet implemented")
      }

      override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
          TODO("Not yet implemented")
      }

      override fun createInvalidationTracker(): InvalidationTracker {
          TODO("Not yet implemented")
      }

      override fun clearAllTables() {
          TODO("Not yet implemented")
      }
  }
    private val repository = RepositoryImpl(db)


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