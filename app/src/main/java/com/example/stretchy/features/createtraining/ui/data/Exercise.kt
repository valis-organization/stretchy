package com.example.stretchy.features.createtraining.ui.data

data class Exercise(
    val id: Int? = null,
    var name: String = "",
    val activityOrder: Int? = null,
    var duration: Int = 10
)

data class BreakAfterExercise(
    var duration: Int? = null
)
