package com.example.stretchy.repository

import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity
import com.example.stretchy.database.entity.TrainingWithActivitiesEntity

class RepositoryImpl(private val db: AppDatabase) : Repository {
    override suspend fun addTrainingWithActivities(training: TrainingWithActivity) {
        val tId = generateTrainingId()
        training.activities.forEach { activity ->
            with(activity) {
                val aId = generateActivityId()
                db.activityDao()
                    .add(ActivityEntity(aId, name, duration, activityType))
                db.trainingWithActivitiesDao()
                    .insert(TrainingActivityEntity(tId, aId))
            }
        }
        with(training) {
            db.trainingDao().add(TrainingEntity(tId, name, trainingType, finished))
        }
    }

    override suspend fun getTrainingsWithActivities(): List<TrainingWithActivity> =
        db.trainingWithActivitiesDao().getTrainings().map { ta ->
            map(ta)
        }

    override suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity {
        val ta = db.trainingWithActivitiesDao().getTrainingsById(id)
        return map(ta)
    }

    private fun map(training: TrainingWithActivitiesEntity): TrainingWithActivity {
        val activitiesMapped: List<Activity> = training.activities
            .map {
                Activity(it.activityId, it.name, it.duration, it.activityType)
            }

        with(training.training) {
            return TrainingWithActivity(
                trainingId,
                name,
                trainingType,
                finished,
                activitiesMapped
            )
        }
    }

    private fun generateActivityId(): Long =
        db.activityDao().getAll().maxOfOrNull { it.activityId + 1 } ?: 0

    private fun generateTrainingId(): Long =
        db.trainingDao().getAll().maxOfOrNull { it.trainingId + 1 } ?: 0
}