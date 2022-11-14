package com.example.stretchy.features.traininglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stretchy.repository.RepositoryImpl
import com.example.stretchy.di.DaggerRepositoryComponent
import com.example.stretchy.di.RepositoryComponent
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrainingListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<TrainingListUiState>(TrainingListUiState.Empty)
    val uiState: StateFlow<TrainingListUiState> = _uiState
    @Inject
     lateinit var repository: RepositoryImpl
    private val repositoryComponent: RepositoryComponent = DaggerRepositoryComponent.create()

    init {
        repositoryComponent.inject(this)
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
