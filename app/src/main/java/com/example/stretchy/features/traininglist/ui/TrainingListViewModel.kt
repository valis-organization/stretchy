package com.example.stretchy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.dataBase.Repository
import com.example.stretchy.dataBase.StretchyDataBase
import com.example.stretchy.ui.theme.ExerciseListUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExercisePlansViewModel : ViewModel() {
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

    sealed class ExercisePlansUiState {
        object Empty : ExercisePlansUiState()
        object Loading : ExercisePlansUiState()
        class Loaded(val data: ExerciseListUiModel) : ExercisePlansUiState()
    }
}


