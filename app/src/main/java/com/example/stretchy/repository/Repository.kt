package com.example.stretchy.repository

import com.example.stretchy.database.data.ExerciseItem
import com.example.stretchy.database.StretchyDataBase
import com.example.stretchy.theme.ExercisePlanItem

class Repository(private val db : StretchyDataBase){

    suspend fun getPlansList(): List<ExercisePlanItem> {
        return db.getPlansList()
    }

    suspend fun getExercisesList() : List<ExerciseItem>{
        return db.getExercisesList()
    }
}