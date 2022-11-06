package com.example.stretchy.database

import com.example.stretchy.database.data.ExerciseItem
import com.example.stretchy.theme.ExercisePlanItem
import com.example.stretchy.features.training.ui.data.ActivityItem

interface DataBase {
    suspend fun getPlansList(): List<ExercisePlanItem>

    suspend fun getExercisesList(): List<ExerciseItem>

    suspend fun addTraining(trainingName: String, activities: List<ActivityItem>)
}