package com.example.stretchy.database

import com.example.stretchy.database.data.ExerciseItem
import com.example.stretchy.theme.ExercisePlanItem
import com.example.stretchy.features.training.ui.data.ActivityItem

class StretchyDataBase : DataBase {

    override suspend fun getPlansList(): List<ExercisePlanItem> {
        return plansList
    }

    override suspend fun getExercisesList(): List<ExerciseItem> {
        return plan1ExercisesList
    }

    override suspend fun addTraining(trainingName: String, activities: List<ActivityItem>) {
        TODO("Not yet implemented")
    }

    private val plan1 = ExercisePlanItem(
        itemName = "Recommendation name",
        numberOfExercises = 4,
        timeInSeconds = 100,
        id = "1"
    )

    private val plan2 = ExercisePlanItem(
        itemName = "Exercise name",
        numberOfExercises = 7,
        timeInSeconds = 203,
        id = "2"
    )

    private val plan1ExercisesList: List<ExerciseItem> =
        mutableListOf(
            ExerciseItem("Exercise 1", 10),
            ExerciseItem("Exercise 2", 20),
            ExerciseItem("Exercise 3", 30),
            ExerciseItem("Exercise 4", 40)
        )

    private val plansList: List<ExercisePlanItem> =
        mutableListOf(plan1, plan2) //temp, in future get it from data base
}