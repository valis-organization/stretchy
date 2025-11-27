package com.example.stretchy.repository

interface Repository {
    suspend fun getTrainingsWithActivities(): List<TrainingWithActivity>
    suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity
    suspend fun addTrainingWithActivities(training: TrainingWithActivity)
    suspend fun editTrainingWithActivities(trainingId: Long, editedTraining: TrainingWithActivity)
    suspend fun deleteTrainingById(trainingId: Long)

    // Break management - breaks are now WorkoutEntity with type=BREAK
    suspend fun findOrCreateBreakWorkout(durationSeconds: Int): Long
}