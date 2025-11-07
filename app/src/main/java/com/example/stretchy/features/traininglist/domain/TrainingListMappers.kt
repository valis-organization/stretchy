// ...existing code...
package com.example.stretchy.features.traininglist.domain

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.repository.Activity
import com.example.stretchy.repository.TrainingWithActivity

fun TrainingWithActivity.toTraining(): Training {
    return Training(
        this.id.toString(),
        this.name,
        this.activities.getExercisesCount(),
        calculateTrainingDuration(activities),
        this.trainingType.toTrainingType()
    )
}

fun calculateTrainingDuration(activities: List<Activity>): Int {
    var duration = 0
    activities.forEach { activity ->
        duration += if (activity.duration == 0 || activity.activityType == ActivityType.TIMELESS_EXERCISE) {
            TIMELESS_EXERCISE_ESTIMATED_DURATION_SECS
        } else {
            activity.duration
        }
    }
    return duration
}

fun List<Activity>.getExercisesCount(): Int {
    var size = 0
    this.forEach {
        if (it.activityType != ActivityType.BREAK) {
            size++
        }
    }
    return size
}

fun TrainingType.toTrainingType(): Training.Type {
    return when (this) {
        TrainingType.STRETCH -> Training.Type.STRETCH
        TrainingType.BODYWEIGHT -> Training.Type.BODY_WEIGHT
    }
}

private const val TIMELESS_EXERCISE_ESTIMATED_DURATION_SECS = 90
// ...existing code...
