// ...existing code...
package com.example.stretchy.features.createtraining.domain

import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.extensions.toActivityType
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity

// Map UI representation (ExercisesWithBreaks) to repository Activity list
fun List<ExercisesWithBreaks>.toActivityList(trainingType: TrainingType): List<Activity> {
    val activityList = mutableListOf<Activity>()
    var activityOrder = 0

    this.forEach {
        with(it.exercise) {
            activityList.add(
                Activity(
                    name,
                    activityOrder,
                    duration,
                    trainingType.toActivityType(duration == 0)
                ).apply { /* keep activityId unset here; repository will manage IDs */ }
            )
        }
        activityOrder++
        if (it.nextBreakDuration != 0 && it.nextBreakDuration != null) {
            activityList.add(
                Activity(
                    "",
                    activityOrder,
                    it.nextBreakDuration!!,
                    ActivityType.BREAK
                )
            )
            activityOrder++
        }
    }
    return activityList
}

// Map repository Activity list to UI representation (ExercisesWithBreaks)
fun List<Activity>.toExercisesWithBreaks(): List<ExercisesWithBreaks> {
    val list = mutableListOf<ExercisesWithBreaks>()
    this.forEachIndexed { index, item ->
        if (item.activityType != ActivityType.BREAK) {
            list.add(
                ExercisesWithBreaks(
                    list.lastIndex + 1,
                    Exercise(
                        item.activityId.toInt(),
                        item.name,
                        item.activityOrder,
                        item.duration
                    ),
                    if (this.getOrNull(index + 1)?.activityType == ActivityType.BREAK) this.getOrNull(index + 1)?.duration
                    else null,
                    false
                )
            )
        }
    }
    return list
}
// ...existing code...
