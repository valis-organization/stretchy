package com.example.stretchy.repository

import android.util.Log
import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.dao.findOrCreateBreak
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.BreakEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity
import com.example.stretchy.database.entity.TrainingWithActivitiesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val MIG_TAG = "MIG_3"

class RepositoryImpl(private val db: AppDatabase) : Repository {
    override suspend fun addTrainingWithActivities(training: TrainingWithActivity) {
        withContext(Dispatchers.IO) {
            val tId = generateTrainingId()
            addTrainingWithActivitiesToDb(training.activities, tId)
            with(training) {
                // TODO: Build proper sequence from activities in later step
                val isDraft = if (finished) null else true
                db.trainingDao().add(TrainingEntity(tId, name, trainingType, isDraft, ""))
            }
        }
    }

    override suspend fun editTrainingWithActivities(
        trainingId: Long,
        editedTraining: TrainingWithActivity
    ) {
        withContext(Dispatchers.IO) {
            deleteActivitiesFromTraining(
                getTrainingWithActivitiesById(trainingId).activities,
                trainingId
            )
            addTrainingWithActivitiesToDb(editedTraining.activities, trainingId)
            with(editedTraining) {
                // TODO: Build proper sequence from activities in later step
                val isDraft = if (finished) null else true
                db.trainingDao().update(TrainingEntity(trainingId, name, trainingType, isDraft, ""))
            }
        }
    }

    override suspend fun deleteTrainingById(trainingId: Long) {
        withContext(Dispatchers.IO) {
            val training = getTrainingWithActivitiesById(trainingId)
            deleteActivitiesFromTraining(training.activities, trainingId)
            db.trainingDao().deleteById(trainingId = trainingId)
        }
    }

    override suspend fun getTrainingsWithActivities(): List<TrainingWithActivity> =
        withContext(Dispatchers.IO) {
            db.trainingWithActivitiesDao().getTrainings().map { training ->
                training.mapToTrainingWithActivity()
            }
        }

    override suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity =
        withContext(Dispatchers.IO) {
            val training = db.trainingWithActivitiesDao().getTrainingsById(id)
            training.mapToTrainingWithActivity()
        }

    private fun TrainingWithActivitiesEntity.mapToTrainingWithActivity(): TrainingWithActivity {
        val trainingId = this.training.trainingId

        // Get training activities with break information
        val trainingActivities = db.trainingWithActivitiesDao().getTrainingsActivitiesByTrainingId(trainingId)
        Log.d(MIG_TAG, "Mapping ${trainingActivities.size} training activities for trainingId=$trainingId")

        val activitiesMapped = mutableListOf<Activity>()

        // Sort activities by order to maintain sequence
        val sortedTrainingActivities = trainingActivities.sortedBy { it.activityOrder }

        sortedTrainingActivities.forEach { trainingActivity ->
            // Find the corresponding activity entity
            val activityEntity = this.activities.find { it.activityId == trainingActivity.aId }
            if (activityEntity != null) {
                // Add the main activity
                val activity = Activity(
                    activityEntity.name,
                    trainingActivity.activityOrder,
                    activityEntity.duration,
                    activityEntity.activityType
                ).apply {
                    this.activityId = activityEntity.activityId
                }
                activitiesMapped.add(activity)

                // If this activity has a break after it, add break as BREAK activity for UI compatibility
                if (trainingActivity.breakId != null) {
                    val breakEntity = db.breakDao().getById(trainingActivity.breakId!!)
                    if (breakEntity != null) {
                        val breakActivity = Activity(
                            "",
                            trainingActivity.activityOrder + 1000, // Use high number to ensure proper sorting
                            breakEntity.duration,
                            com.example.stretchy.database.data.ActivityType.BREAK
                        ).apply {
                            this.activityId = 0 // Breaks don't have real activity IDs
                        }
                        activitiesMapped.add(breakActivity)
                        Log.d(MIG_TAG, "Added break activity: duration=${breakEntity.duration} after activity order=${trainingActivity.activityOrder}")
                    }
                }
            }
        }

        // Re-sort and reassign activity orders for UI compatibility
        activitiesMapped.sortedBy { it.activityOrder }.forEachIndexed { index, activity ->
            activity.activityOrder = index
        }

        with(this.training) {
            return TrainingWithActivity(
                name,
                trainingType,
                isDraft != true, // isDraft=null or false means finished=true
                activitiesMapped.sortedBy { it.activityOrder }
            ).apply { id = trainingId }
        }
    }

    private fun generateActivityId(): Long =
        db.activityDao().getAll().maxOfOrNull { it.activityId + 1 } ?: 0

    private fun generateTrainingId(): Long =
        db.trainingDao().getAll().maxOfOrNull { it.trainingId + 1 } ?: 0

    private fun deleteActivitiesFromTraining(activities: List<Activity>, trainingId: Long) {
        // Collect break IDs that will be orphaned
        val trainingActivities = db.trainingWithActivitiesDao().getTrainingsActivitiesByTrainingId(trainingId)
        val breakIdsToCheck = trainingActivities.mapNotNull { it.breakId }.distinct()

        activities.forEach { activity ->
            if (activity.activityType != com.example.stretchy.database.data.ActivityType.BREAK) {
                with(activity) {
                    db.trainingWithActivitiesDao()
                        .delete(TrainingActivityEntity(trainingId, activityId, activityOrder!!, null))
                }
            }
        }

        // Clean up unused breaks (run in background scope since this is non-suspend)
        breakIdsToCheck.forEach { breakId ->
            val usageCount = db.breakDao().getUsageCount(breakId)
            if (usageCount == 0) {
                db.breakDao().deleteById(breakId)
                Log.d(MIG_TAG, "Deleted unused break: id=$breakId")
            }
        }
        Log.d(MIG_TAG, "Cleaned up activities and checked ${breakIdsToCheck.size} breaks for deletion")
    }

    private fun addTrainingWithActivitiesToDb(activities: List<Activity>, trainingId: Long) {
        val activitiesOnly = activities.filter { it.activityType != com.example.stretchy.database.data.ActivityType.BREAK }

        activitiesOnly.forEachIndexed { index, activity ->
            with(activity) {
                // Add the activity entity
                var aId = generateActivityId()
                val result = db.activityDao()
                    .add(ActivityEntity(aId, name, duration, activityType))
                if (result == -1L) {
                    aId = db.activityDao().getConflictActivity(name, duration)?.activityId ?: aId
                }

                // Check if there's a break after this activity
                val nextActivity = activities.getOrNull(activities.indexOf(activity) + 1)
                val breakId = if (nextActivity?.activityType == com.example.stretchy.database.data.ActivityType.BREAK) {
                    Log.d(MIG_TAG, "Processing break after activity: duration=${nextActivity.duration}")
                    val breakEntity = db.breakDao().findOrCreateBreak(nextActivity.duration)
                    Log.d(MIG_TAG, "Break entity created/found: id=${breakEntity.breakId}, duration=${nextActivity.duration}")
                    breakEntity.breakId
                } else {
                    null
                }

                // Insert training activity with break reference
                db.trainingWithActivitiesDao()
                    .insert(TrainingActivityEntity(trainingId, aId, index, breakId))

                Log.d(MIG_TAG, "Inserted activity: trainingId=$trainingId, aId=$aId, order=$index, breakId=$breakId")
            }
        }
    }

    // Break management methods implementation
    override suspend fun findOrCreateBreak(duration: Int): BreakEntity =
        withContext(Dispatchers.IO) {
            db.breakDao().findOrCreateBreak(duration)
        }

    override suspend fun editBreakSmart(currentBreakId: Long?, newDuration: Int?): Long? =
        withContext(Dispatchers.IO) {
            Log.d(MIG_TAG, "Editing break: currentId=$currentBreakId, newDuration=$newDuration")

            // If no new duration, remove break
            if (newDuration == null) {
                if (currentBreakId != null) {
                    deleteBreakIfUnused(currentBreakId)
                }
                return@withContext null
            }

            // If no current break, create new one
            if (currentBreakId == null) {
                val newBreak = db.breakDao().findOrCreateBreak(newDuration)
                Log.d(MIG_TAG, "Created new break for duration $newDuration: id=${newBreak.breakId}")
                return@withContext newBreak.breakId
            }

            // Check if current break has the desired duration
            val currentBreak = db.breakDao().getById(currentBreakId)
            if (currentBreak?.duration == newDuration) {
                Log.d(MIG_TAG, "Break duration unchanged: id=$currentBreakId, duration=$newDuration")
                return@withContext currentBreakId
            }

            // Check usage count of current break
            val usageCount = db.breakDao().getUsageCount(currentBreakId)

            if (usageCount <= 1) {
                // Only one usage, safe to update in place
                if (currentBreak != null) {
                    val updatedBreak = currentBreak.copy(duration = newDuration)
                    db.breakDao().update(updatedBreak)
                    Log.d(MIG_TAG, "Updated break in place: id=$currentBreakId, duration=$newDuration")
                    return@withContext currentBreakId
                }
            }

            // Multiple usages, find or create break with new duration
            val newBreak = db.breakDao().findOrCreateBreak(newDuration)
            Log.d(MIG_TAG, "Switched to existing/new break: from id=$currentBreakId to id=${newBreak.breakId}, duration=$newDuration")
            return@withContext newBreak.breakId
        }

    override suspend fun deleteBreakIfUnused(breakId: Long): Boolean =
        withContext(Dispatchers.IO) {
            val usageCount = db.breakDao().getUsageCount(breakId)
            Log.d(MIG_TAG, "Checking break deletion: id=$breakId, usageCount=$usageCount")

            if (usageCount == 0) {
                db.breakDao().deleteById(breakId)
                Log.d(MIG_TAG, "Deleted unused break: id=$breakId")
                return@withContext true
            } else {
                Log.d(MIG_TAG, "Break still in use, not deleted: id=$breakId")
                return@withContext false
            }
        }
}