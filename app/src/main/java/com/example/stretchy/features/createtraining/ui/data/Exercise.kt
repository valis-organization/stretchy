package com.example.stretchy.features.createtraining.ui.data

data class Exercise(
    var name: String = "",
    val activityOrder: Int? = null,
    var duration: Int = 10,
    val listId: Int? = null,
)

data class BreakAfterExercise(
    var duration: Int? = null
)
