package com.example.stretchy.features.createtraining.ui

import com.example.stretchy.repository.Activity

sealed class CreateTrainingUiState(open val createTrainingButtonVisible: Boolean,open val isTrainingChanged : Boolean) {
    data class Success(
        val trainingId: Long?,
        val editingTraining: Boolean,
        val currentName: String,
        val activities: List<Activity>,
        override val isTrainingChanged : Boolean = false,
        override val createTrainingButtonVisible: Boolean = false
    ) : CreateTrainingUiState(createTrainingButtonVisible,isTrainingChanged)

    data class Error(val reason: Reason) : CreateTrainingUiState(false,false) {
        sealed class Reason {
            object MissingTrainingName : Reason()
            object NotEnoughExercises : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    object Init : CreateTrainingUiState(false,false)

    object Done : CreateTrainingUiState(false,false)
}

sealed class CreateTrainingActivityItem(
    open val duration: Float,
) {
    data class Exercise(
        val name: String,
        override val duration: Float,
    ) : CreateTrainingActivityItem(duration)

    data class Break(
        override val duration: Float,
    ) : CreateTrainingActivityItem(duration)
}