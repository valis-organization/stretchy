package com.example.stretchy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.dataBase.StretchyDataBase
import com.example.stretchy.ui.theme.data.ActivityItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CrateTrainingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<TrainingState> = MutableStateFlow(TrainingState.Init)
    val uiState: StateFlow<TrainingState> = _uiState
    private val db = StretchyDataBase()
    private val activities: MutableList<ActivityItem> = mutableListOf()
    private var trainingName: String? = null

    fun addActivity(activityItem: ActivityItem) {
        activities.add(activityItem)

    }

    fun replaceActivities(firstPos: Int, secondPos: Int) {
        val firstToReplace = activities[firstPos]
        activities[firstPos] = activities[secondPos]
        activities[secondPos] = firstToReplace
        viewModelScope.launch {
            _uiState.emit(TrainingState.Success(activities))
        }
    }

    fun setTrainingName(trainingName: String) {
        this.trainingName = trainingName
    }

    fun createTraining() {
        viewModelScope.launch {
            if (trainingName == null) {
                _uiState.emit(TrainingState.Error(TrainingState.Error.Reason.MissingTrainingName))
            } else if (activities.size < 2) {
                _uiState.emit(TrainingState.Error(TrainingState.Error.Reason.NotEnoughExercises))
            } else {
                try {
                    db.addTraining(trainingName!!, activities)
                    _uiState.emit(TrainingState.Done)
                } catch (ex: Exception) {
                    _uiState.emit(TrainingState.Error(TrainingState.Error.Reason.Unknown(ex)))
                }
            }
        }
    }

    sealed class TrainingState {
        data class Success(
            val training: List<ActivityItem>
        ) : TrainingState()

        data class Error(val reason: Reason) : TrainingState() {
            sealed class Reason {
                object MissingTrainingName : Reason()
                object NotEnoughExercises : Reason()
                class Unknown(val exception: Exception) : Reason()
            }
        }

        object Init : TrainingState()

        object Done : TrainingState()
    }
}