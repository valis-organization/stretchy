package com.example.stretchy.ui.theme.data

sealed class ActivityItem {
    abstract val nextExercise: String
    abstract val currentTime: Int
    abstract val totalTime: Int
}

data class Exercise(
    val exerciseName: String,
    override val nextExercise: String,
    override val currentTime: Int,
    override val totalTime: Int
) : ActivityItem()

data class Break(
    override val nextExercise: String,
    override val currentTime: Int,
    override val totalTime: Int
) : ActivityItem()

