package com.example.stretchy.features.traininglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.database.DataBase
import com.example.stretchy.repository.RepositoryImpl
import com.example.stretchy.database.MockedDataBaseImpl
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<TrainingListUiState>(TrainingListUiState.Empty)
    val uiState: StateFlow<TrainingListUiState> = _uiState
    private val db: DataBase = MockedDataBaseImpl()
    private val repository: Repository = RepositoryImpl(db)

    init {
        fetchTrainingList()
    }

    private fun fetchTrainingList() {
        _uiState.value = TrainingListUiState.Loading
        viewModelScope.launch {
            val trainingList = repository.getTrainings()
            if (trainingList.isEmpty()) {
                _uiState.value = TrainingListUiState.Empty
            } else {
                _uiState.value = TrainingListUiState.Loaded(trainingList)
            }
        }
    }
}
