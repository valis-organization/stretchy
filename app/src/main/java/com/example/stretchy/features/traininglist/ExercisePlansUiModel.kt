package com.example.stretchy.ui.theme

data class ExerciseListUiModel(
    var exercisePlans: List<ExercisePlanItem>
)

data class ExercisePlanItem(
    val id: String,
    val itemName: String,
    val numberOfExercises: Int,
    val timeInSeconds: Int
)
