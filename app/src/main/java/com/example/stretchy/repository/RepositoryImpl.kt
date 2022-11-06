package com.example.stretchy.repository

import com.example.stretchy.database.DataBase
import com.example.stretchy.database.MockedDataBaseImpl
import com.example.stretchy.features.traininglist.ui.data.Training

class RepositoryImpl(private val db: DataBase) : Repository{

    override suspend fun getTrainings(): List<Training> {
        return db.getTrainings()
    }

    override suspend fun getActivitiesForTraining(id: String): List<ActivityDomain> {
        val exerciseDb = db.getExercisesForTraining(id)
        val activities = mutableListOf<ActivityDomain>()
        exerciseDb.forEachIndexed { index, it ->
            activities.add(ActivityDomain.ExerciseDomain(it.name, it.duration))
            if (index != exerciseDb.lastIndex) {
                activities.add(ActivityDomain.BreakDomain(5))
            }
        }
        return activities
    }
}
