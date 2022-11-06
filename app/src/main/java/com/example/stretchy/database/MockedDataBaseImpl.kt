package com.example.stretchy.database

import com.example.stretchy.database.data.ExerciseItemDb
import com.example.stretchy.features.traininglist.ui.data.Training

class MockedDataBaseImpl : DataBase {
    override suspend fun getTrainings(): List<Training> {
        return trainingsMock
    }

    override suspend fun getExercisesForTraining(training: String): List<ExerciseItemDb> {
        return training1Exercises
    }

    override suspend fun addTraining(trainingName: String, exercises: List<ExerciseItemDb>) {
        TODO("Not yet implemented")
    }

    private val training1 = Training(
        itemName = "Cat Cow Stretch",
        numberOfExercises = 4,
        timeInSeconds = 100,
        id = "1"
    )

    private val training2 = Training(
        itemName = "Leg Stretch L",
        numberOfExercises = 7,
        timeInSeconds = 203,
        id = "2"
    )

    private val training1Exercises: List<ExerciseItemDb> =
        mutableListOf(
            ExerciseItemDb("Exercise 1", 10),
            ExerciseItemDb("Exercise 2", 20),
            ExerciseItemDb("Exercise 3", 30),
            ExerciseItemDb("Exercise 4", 40)
        )

    private val trainingsMock: List<Training> =
        mutableListOf(training1, training2) //temp, in future get it from data base
}