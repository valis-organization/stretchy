package com.example.stretchy.ui.theme

sealed class ActivityItem(
    open val nextExercise: String?,
    open val currentTime: Float,
    open val totalTime: Int,
) {
    data class Exercise(
        val exerciseName: String,
        override val nextExercise: String?,
        override val currentTime: Float,
        override val totalTime: Int
    ) : ActivityItem(nextExercise, currentTime, totalTime)

    data class Break(
        override val nextExercise: String,
        override val currentTime: Float,
        override val totalTime: Int
    ) : ActivityItem(nextExercise, currentTime, totalTime)

}
