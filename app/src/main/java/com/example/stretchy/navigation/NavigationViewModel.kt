package com.example.stretchy.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NavigationViewModel : ViewModel() {

    private val _navEvents = MutableSharedFlow<NavEvent>()
    val navEvents: SharedFlow<NavEvent> = _navEvents.asSharedFlow()

    sealed class NavEvent {
        data class CreateTraining(val trainingType: String) : NavEvent()
        data class EditTraining(val trainingId: String, val trainingType: String) : NavEvent()
        data class ExecuteTraining(val trainingId: String) : NavEvent()
        object Back : NavEvent()
        data class NavToRoute(val route: String) : NavEvent()
    }

    fun navigateToCreateTraining(trainingType: String) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.CreateTraining(trainingType))
        }
    }

    fun navigateToEditTraining(trainingId: String, trainingType: String) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.EditTraining(trainingId, trainingType))
        }
    }

    fun navigateToExecuteTraining(trainingId: String) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.ExecuteTraining(trainingId))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.Back)
        }
    }

    fun navigateToRoute(route: String) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.NavToRoute(route))
        }
    }
}
