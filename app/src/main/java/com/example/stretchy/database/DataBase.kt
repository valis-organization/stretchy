package com.example.stretchy.database

import com.example.stretchy.database.data.ExerciseItemDb
import com.example.stretchy.features.traininglist.ui.data.Training

interface DataBase {
    suspend fun getTrainings(): List<Training>

    suspend fun addTraining(trainingName: String, exercises: List<ExerciseItemDb>)

    suspend fun getExercisesForTraining(training: String): List<ExerciseItemDb>
}