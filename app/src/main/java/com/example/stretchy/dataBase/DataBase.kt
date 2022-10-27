package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExerciseItem

interface DataBase {
    suspend fun getExercisesList(): List<ExerciseItem>
}