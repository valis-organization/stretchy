package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExercisePlanItem

interface DataBase {
    suspend fun getPlansList(): List<ExercisePlanItem>

    suspend fun getExercisesList() : List<ExerciseItemPOJO>
}