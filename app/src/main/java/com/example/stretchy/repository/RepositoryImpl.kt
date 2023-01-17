package com.example.stretchy.repository

import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity
import com.example.stretchy.database.entity.TrainingWithActivitiesEntity

class RepositoryImpl(private val db: AppDatabase) : Repository {
    override suspend fun addTrainingWithActivities(training: TrainingWithActivity) {
        val tId = generateTrainingId()
        addTrainingWithActivitiesToDb(training.activities, tId)
        with(training) {
            db.trainingDao().add(TrainingEntity(tId, name, trainingType, finished))
        }
    }

    override suspend fun editTrainingWithActivities(
        trainingId: Long,
        editedTraining: TrainingWithActivity
    ) {
        deleteActivitiesFromTraining(
            getTrainingWithActivitiesById(trainingId).activities,
            trainingId
        )
        addTrainingWithActivitiesToDb(editedTraining.activities, trainingId)
        with(editedTraining) {
            db.trainingDao().update(TrainingEntity(trainingId, name, trainingType, finished))
        }
    }

    override suspend fun deleteTrainingById(trainingId: Long) {
        val training = getTrainingWithActivitiesById(trainingId)
        deleteActivitiesFromTraining(training.activities, trainingId)
        db.trainingDao().deleteById(trainingId = trainingId)
    }

    override suspend fun getTrainingsWithActivities(): List<TrainingWithActivity> =
        db.trainingWithActivitiesDao().getTrainings().map { activity ->
            map(activity)
        }

    override suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity {
        val activity = db.trainingWithActivitiesDao().getTrainingsById(id)
        return map(activity)
    }

    private fun map(training: TrainingWithActivitiesEntity): TrainingWithActivity {
        val activitiesMapped: List<Activity> = training.activities
            .map {
                Activity(it.name, it.duration, it.activityType).apply {
                    this.activityId = it.activityId
                }
            }

        with(training.training) {
            return TrainingWithActivity(
                name,
                trainingType,
                finished,
                activitiesMapped
            ).apply { id = trainingId }
        }
    }

    private fun generateActivityId(): Long =
        db.activityDao().getAll().maxOfOrNull { it.activityId + 1 } ?: 0

    private fun generateTrainingId(): Long =
        db.trainingDao().getAll().maxOfOrNull { it.trainingId + 1 } ?: 0

    private fun deleteActivitiesFromTraining(activities: List<Activity>, trainingId: Long) {
        activities.forEach { activity ->
            with(activity) {
                db.activityDao().delete(ActivityEntity(activityId, name, duration, activityType))
                db.trainingWithActivitiesDao()
                    .delete(TrainingActivityEntity(trainingId, activityId))
            }
        }
    }

    private fun addTrainingWithActivitiesToDb(activities: List<Activity>, trainingId: Long) {
        activities.forEach { activity ->
            with(activity) {
                val aId = generateActivityId()
                db.activityDao().add(ActivityEntity(aId, name, duration, activityType))
                db.trainingWithActivitiesDao().insert(TrainingActivityEntity(trainingId, aId))
            }
        }
    }
}