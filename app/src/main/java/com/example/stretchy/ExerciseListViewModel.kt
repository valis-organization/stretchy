package com.example.stretchy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.dataBase.Repository
import com.example.stretchy.ui.theme.ExerciseListUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ExerciseListUiState>(ExerciseListUiState.Empty)
    val uiState: StateFlow<ExerciseListUiState> = _uiState

    init {
        fetchExercisesList()
    }

    private fun fetchExercisesList() {
        _uiState.value = ExerciseListUiState.Loading
        viewModelScope.launch {
            val exercisesList = Repository.getExercisesList()
            if (exercisesList.isEmpty()) {
                _uiState.value = ExerciseListUiState.Empty
            } else {
                _uiState.value = ExerciseListUiState.Loaded(ExerciseListUiModel(exercisesList))
            }
        }
    }

    sealed class ExerciseListUiState {
        object Empty : ExerciseListUiState()
        object Loading : ExerciseListUiState()
        class Loaded(val data: ExerciseListUiModel) : ExerciseListUiState()
    }
}
