package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExercisePlanItem

class Repository(private val db : StretchyDataBase){

    suspend fun getExercisesList(): List<ExercisePlanItem> {
        return db.getExercisesList()
    }
}