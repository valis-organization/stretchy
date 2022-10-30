package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExercisePlanItem

interface DataBase {
    suspend fun getExercisesList(): List<ExercisePlanItem>
}