package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExerciseItem

class Repository {
    private val db = DataBaseImpl()

    suspend fun getExercisesList(): List<ExerciseItem> {
        return db.getExercisesList()
    }
}