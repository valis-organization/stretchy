package com.example.stretchy.ui.theme

data class ExerciseListUiModel(
    var exerciseList: List<ExerciseItem>
)

data class ExerciseItem(
    val itemName: String,
    val numberOfExercises: Int,
    val timeInSeconds: Int
)
