package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExercisePlanItem

class Repository(private val db : StretchyDataBase){

    suspend fun getPlansList(): List<ExercisePlanItem> {
        return db.getPlansList()
    }

    suspend fun getExercisesList() : List<ExerciseItem>{
        return db.getExercisesList()
    }
}