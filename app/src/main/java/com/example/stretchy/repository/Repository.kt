package com.example.stretchy.repository


interface Repository {
    suspend fun getTrainingsWithActivities(): List<TrainingWithActivity>
    suspend fun getTrainingWithActivitiesById(long: Long): TrainingWithActivity
    suspend fun addTrainingWithActivities(training: TrainingWithActivity)
}