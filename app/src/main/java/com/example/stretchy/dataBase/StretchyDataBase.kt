package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExercisePlanItem

class StretchyDataBase : DataBase {

    private val exercise1 = ExercisePlanItem(
        itemName = "Recommendation name",
        numberOfExercises = 11,
        timeInSeconds = 397
    )

    private val exercise2 = ExercisePlanItem(
        itemName = "Exercise name",
        numberOfExercises = 7,
        timeInSeconds = 203
    )

    private val exercisesList: List<ExercisePlanItem> =
        mutableListOf(exercise1, exercise2) //temp, in future get it from data base

    override suspend fun getExercisesList(): List<ExercisePlanItem> {
        return exercisesList
    }
}