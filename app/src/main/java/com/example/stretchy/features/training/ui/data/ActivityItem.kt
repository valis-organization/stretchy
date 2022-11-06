package com.example.stretchy.features.training.ui.data

sealed class ActivityItem {
    abstract val nextExercise: String
    abstract val currentTime: Float
    abstract val totalTime: Int
}

data class Exercise(
    val exerciseName: String,
    override val nextExercise: String,
    override val currentTime: Float,
    override val totalTime: Int
) : ActivityItem()

data class Break(
    override val nextExercise: String,
    override val currentTime: Float,
    override val totalTime: Int
) : ActivityItem()

