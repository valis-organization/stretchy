package com.example.stretchy.repository

import com.example.stretchy.database.entity.BreakEntity

interface Repository {
    suspend fun getTrainingsWithActivities(): List<TrainingWithActivity>
    suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity
    suspend fun addTrainingWithActivities(training: TrainingWithActivity)
    suspend fun editTrainingWithActivities(trainingId: Long, editedTraining: TrainingWithActivity)
    suspend fun deleteTrainingById(trainingId: Long)

    // Break management methods
    suspend fun findOrCreateBreak(duration: Int): BreakEntity
    suspend fun editBreakSmart(currentBreakId: Long?, newDuration: Int?): Long?
    suspend fun deleteBreakIfUnused(breakId: Long): Boolean
}