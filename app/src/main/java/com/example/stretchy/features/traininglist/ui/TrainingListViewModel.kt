package com.example.stretchy.features.traininglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.repository.Repository
import com.example.stretchy.database.StretchyDataBase
import com.example.stretchy.theme.ExerciseListUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ExercisePlansUiState>(ExercisePlansUiState.Empty)
    val uiState: StateFlow<ExercisePlansUiState> = _uiState
    private val db = StretchyDataBase()
    private val repository = Repository(db) //In future inject it

    init {
        fetchPlansList()
    }

    private fun fetchPlansList() {
        _uiState.value = ExercisePlansUiState.Loading
        viewModelScope.launch {
            val plansList = repository.getPlansList()
            if (plansList.isEmpty()) {
                _uiState.value = ExercisePlansUiState.Empty
            } else {
                _uiState.value = ExercisePlansUiState.Loaded(ExerciseListUiModel(plansList))
            }
        }
    }
}
