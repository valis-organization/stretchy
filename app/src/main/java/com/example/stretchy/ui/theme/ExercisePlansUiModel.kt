package com.example.stretchy.ui.theme

data class ExerciseListUiModel(
    var exercisePlans: List<ExercisePlanItem>
)

data class ExercisePlanItem(
    val itemName: String,
    val numberOfExercises: Int,
    val timeInSeconds: Int
)
