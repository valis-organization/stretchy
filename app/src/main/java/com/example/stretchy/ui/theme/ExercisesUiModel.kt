package com.example.stretchy.ui.theme

data class ExercisesUiModel(val exerciseList: List<ExerciseItem>)

data class ExerciseItem(
    val exerciseName: String,
    val exerciseTimeLength: Long
)
