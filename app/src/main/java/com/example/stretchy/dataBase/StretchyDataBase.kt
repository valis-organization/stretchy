package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExercisePlanItem

class StretchyDataBase : DataBase {

    private val plan1 = ExercisePlanItem(
        itemName = "Recommendation name",
        numberOfExercises = 4,
        timeInSeconds = 100
    )

    private val plan2 = ExercisePlanItem(
        itemName = "Exercise name",
        numberOfExercises = 7,
        timeInSeconds = 203
    )

    private val plan1ExercisesList: List<ExerciseItemPOJO> =
        mutableListOf(
            ExerciseItemPOJO("Exercise 1", 10),
            ExerciseItemPOJO("Exercise 2", 20),
            ExerciseItemPOJO("Exercise 3", 30),
            ExerciseItemPOJO("Exercise 4", 40)
        )

    private val plansList: List<ExercisePlanItem> =
        mutableListOf(plan1, plan2) //temp, in future get it from data base

    override suspend fun getPlansList(): List<ExercisePlanItem> {
        return plansList
    }

    override suspend fun getExercisesList(): List<ExerciseItemPOJO> {
        return plan1ExercisesList
    }
}