package com.example.stretchy.repository

import android.util.Log
import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.entity.TrainingEntity
import com.example.stretchy.database.entity.WorkoutEntity
import com.example.stretchy.database.data.WorkoutType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val REPO_TAG = "REPOSITORY"

class RepositoryImpl(private val db: AppDatabase) : Repository {
    override suspend fun addTrainingWithActivities(training: TrainingWithActivity) {
        withContext(Dispatchers.IO) {
            // Validate input
            if (training.activities.isEmpty()) {
                Log.e(REPO_TAG, "Cannot add training with empty activities list")
                throw IllegalArgumentException("Training must have at least one activity")
            }

            // Use transaction for data integrity
            db.runInTransaction {
                val tId = generateTrainingId()

                // Create/reuse workouts and build sequence
                val workoutIds = mutableListOf<Long>()

                training.activities.forEach { activity ->
                    val workoutType = when (activity.activityType) {
                        com.example.stretchy.database.data.ActivityType.STRETCH -> WorkoutType.STRETCH
                        com.example.stretchy.database.data.ActivityType.EXERCISE,
                        com.example.stretchy.database.data.ActivityType.TIMELESS_EXERCISE -> WorkoutType.BODYWEIGHT
                        com.example.stretchy.database.data.ActivityType.BREAK -> WorkoutType.BREAK
                    }

                    val workoutId = findOrCreateWorkout(
                        name = activity.name,
                        durationSeconds = activity.duration,
                        workoutType = workoutType
                    )

                    workoutIds.add(workoutId)
                    Log.d(REPO_TAG, "Added workout to sequence: id=$workoutId, name=${activity.name}, type=$workoutType")
                }

                // Build comma-separated sequence
                val sequence = workoutIds.joinToString(",")
                Log.d(REPO_TAG, "Built sequence for training $tId: $sequence")

                // Validate sequence integrity before saving
                if (!validateSequenceIntegrity(sequence)) {
                    throw IllegalStateException("Sequence validation failed for training $tId")
                }

                // Save training with sequence
                with(training) {
                    val isDraft = if (finished) null else true
                    db.trainingDao().add(TrainingEntity(tId, name, trainingType, isDraft, sequence))
                }
            }
        }
    }

    override suspend fun editTrainingWithActivities(
        trainingId: Long,
        editedTraining: TrainingWithActivity
    ) {
        withContext(Dispatchers.IO) {
            // Validate input
            if (editedTraining.activities.isEmpty()) {
                Log.e(REPO_TAG, "Cannot edit training with empty activities list")
                throw IllegalArgumentException("Training must have at least one activity")
            }

            // Get old training data before transaction
            val oldTraining = db.trainingDao().getById(trainingId)
            val oldSequence = oldTraining?.sequence ?: ""

            // Use transaction for data integrity
            db.runInTransaction {
                // Create/reuse workouts and build new sequence
                val workoutIds = mutableListOf<Long>()

                editedTraining.activities.forEach { activity ->
                    val workoutType = when (activity.activityType) {
                        com.example.stretchy.database.data.ActivityType.STRETCH -> WorkoutType.STRETCH
                        com.example.stretchy.database.data.ActivityType.EXERCISE,
                        com.example.stretchy.database.data.ActivityType.TIMELESS_EXERCISE -> WorkoutType.BODYWEIGHT
                        com.example.stretchy.database.data.ActivityType.BREAK -> WorkoutType.BREAK
                    }

                    val workoutId = findOrCreateWorkout(
                        name = activity.name,
                        durationSeconds = activity.duration,
                        workoutType = workoutType
                    )

                    workoutIds.add(workoutId)
                }

                // Build comma-separated sequence
                val newSequence = workoutIds.joinToString(",")
                Log.d(REPO_TAG, "Updated sequence for training $trainingId: old=$oldSequence, new=$newSequence")

                // Validate sequence integrity before saving
                if (!validateSequenceIntegrity(newSequence)) {
                    throw IllegalStateException("Sequence validation failed for training $trainingId")
                }

                // Update training with new sequence
                with(editedTraining) {
                    val isDraft = if (finished) null else true
                    db.trainingDao().update(TrainingEntity(trainingId, name, trainingType, isDraft, newSequence))
                }

                // Clean up orphaned workouts from old sequence
                cleanupOrphanedWorkouts(oldSequence, newSequence)
            }
        }
    }

    override suspend fun deleteTrainingById(trainingId: Long) {
        withContext(Dispatchers.IO) {
            // Get training data before transaction
            val training = db.trainingDao().getById(trainingId)
            val sequence = training?.sequence ?: ""

            // Use transaction for data integrity
            db.runInTransaction {
                // Delete training
                db.trainingDao().deleteById(trainingId = trainingId)

                // Clean up orphaned workouts
                if (sequence.isNotEmpty()) {
                    cleanupOrphanedWorkouts(sequence, "")
                }
            }
        }
    }

    override suspend fun getTrainingsWithActivities(): List<TrainingWithActivity> =
        withContext(Dispatchers.IO) {
            db.trainingDao().getAll().map { trainingEntity ->
                mapTrainingEntityToTrainingWithActivity(trainingEntity)
            }
        }

    override suspend fun getTrainingWithActivitiesById(id: Long): TrainingWithActivity =
        withContext(Dispatchers.IO) {
            val trainingEntity = db.trainingDao().getById(id)
                ?: throw IllegalArgumentException("Training with id $id not found")

            return@withContext mapTrainingEntityToTrainingWithActivity(trainingEntity)
        }

    private fun generateTrainingId(): Long =
        db.trainingDao().getAll().maxOfOrNull { it.trainingId + 1 } ?: 0

    // Break management - breaks are now WorkoutEntity with type=BREAK
    override suspend fun findOrCreateBreakWorkout(durationSeconds: Int): Long =
        withContext(Dispatchers.IO) {
            Log.d(REPO_TAG, "Finding or creating break workout with duration=$durationSeconds")

            // Find or create workout with type BREAK
            val workoutId = findOrCreateWorkout(
                name = "", // Breaks have empty name
                durationSeconds = durationSeconds,
                workoutType = WorkoutType.BREAK
            )

            Log.d(REPO_TAG, "Break workout id=$workoutId for duration=$durationSeconds")
            return@withContext workoutId
        }

    /**
     * Find existing workout or create new one. Implements workout reusability.
     */
    private fun findOrCreateWorkout(name: String, durationSeconds: Int, workoutType: WorkoutType): Long {
        // Check if workout with same parameters already exists
        val existingWorkouts = db.workoutDao().getAll()
        val existingWorkout = existingWorkouts.find {
            it.name == name &&
            it.durationSeconds == durationSeconds &&
            it.workoutType == workoutType
        }

        if (existingWorkout != null) {
            Log.d(REPO_TAG, "Reusing existing workout: id=${existingWorkout.workoutId}, name=$name")
            return existingWorkout.workoutId
        }

        // Create new workout
        val newWorkout = WorkoutEntity(
            name = name,
            durationSeconds = durationSeconds,
            workoutType = workoutType
        )
        val workoutId = db.workoutDao().insert(newWorkout)
        Log.d(REPO_TAG, "Created new workout: id=$workoutId, name=$name, type=$workoutType")
        return workoutId
    }

    /**
     * Map TrainingEntity with sequence to TrainingWithActivity for UI.
     */
    private fun mapTrainingEntityToTrainingWithActivity(trainingEntity: TrainingEntity): TrainingWithActivity {
        val workoutIds = if (trainingEntity.sequence.isEmpty()) {
            emptyList()
        } else {
            trainingEntity.sequence.split(",").mapNotNull { it.toLongOrNull() }
        }

        Log.d(REPO_TAG, "Mapping training ${trainingEntity.trainingId} with ${workoutIds.size} workouts")

        val activities = workoutIds.mapIndexed { index, workoutId ->
            val workout = db.workoutDao().getById(workoutId)
            if (workout != null) {
                val activityType = when (workout.workoutType) {
                    WorkoutType.STRETCH -> com.example.stretchy.database.data.ActivityType.STRETCH
                    WorkoutType.BODYWEIGHT -> com.example.stretchy.database.data.ActivityType.EXERCISE
                    WorkoutType.BREAK -> com.example.stretchy.database.data.ActivityType.BREAK
                }

                Activity(
                    name = workout.name,
                    activityOrder = index,
                    duration = workout.durationSeconds,
                    activityType = activityType
                ).apply {
                    this.activityId = workout.workoutId
                }
            } else {
                Log.e(REPO_TAG, "Workout not found: id=$workoutId in training ${trainingEntity.trainingId}")
                null
            }
        }.filterNotNull()

        return TrainingWithActivity(
            name = trainingEntity.name,
            trainingType = trainingEntity.trainingType,
            finished = trainingEntity.isDraft != true,
            activities = activities
        ).apply {
            id = trainingEntity.trainingId
        }
    }

    /**
     * Clean up workouts that are no longer used in any training sequence.
     */
    private fun cleanupOrphanedWorkouts(oldSequence: String, newSequence: String) {
        val oldIds = if (oldSequence.isEmpty()) emptySet() else oldSequence.split(",").mapNotNull { it.toLongOrNull() }.toSet()
        val newIds = if (newSequence.isEmpty()) emptySet() else newSequence.split(",").mapNotNull { it.toLongOrNull() }.toSet()

        // Find IDs that were removed
        val removedIds = oldIds - newIds

        if (removedIds.isEmpty()) {
            Log.d(REPO_TAG, "No workouts to clean up")
            return
        }

        Log.d(REPO_TAG, "Checking ${removedIds.size} potentially orphaned workouts")

        // For each removed workout, check if it's still used in other trainings
        val allTrainings = db.trainingDao().getAll()

        removedIds.forEach { workoutId ->
            val stillUsed = allTrainings.any { training ->
                training.sequence.split(",").mapNotNull { it.toLongOrNull() }.contains(workoutId)
            }

            if (!stillUsed) {
                db.workoutDao().deleteById(workoutId)
                Log.d(REPO_TAG, "Deleted orphaned workout: id=$workoutId")
            } else {
                Log.d(REPO_TAG, "Workout still in use: id=$workoutId")
            }
        }
    }

    /**
     * Validate that all workout IDs in sequence exist in database.
     * This acts as a foreign key constraint check.
     */
    private fun validateSequenceIntegrity(sequence: String): Boolean {
        if (sequence.isEmpty()) {
            Log.e(REPO_TAG, "Sequence is empty - validation failed")
            return false
        }

        val workoutIds = sequence.split(",").mapNotNull { it.toLongOrNull() }

        if (workoutIds.isEmpty()) {
            Log.e(REPO_TAG, "No valid workout IDs in sequence: $sequence")
            return false
        }

        // Check that all workout IDs exist
        val existingWorkouts = db.workoutDao().getByIds(workoutIds)
        val existingIds = existingWorkouts.map { it.workoutId }.toSet()

        val missingIds = workoutIds.filter { !existingIds.contains(it) }

        if (missingIds.isNotEmpty()) {
            Log.e(REPO_TAG, "Sequence validation failed: missing workout IDs: $missingIds")
            return false
        }

        Log.d(REPO_TAG, "Sequence validation passed: all ${workoutIds.size} workouts exist")
        return true
    }

    /**
     * Verify data integrity across all trainings.
     * Can be called periodically or after migrations.
     */
    suspend fun verifyDatabaseIntegrity(): DatabaseIntegrityReport = withContext(Dispatchers.IO) {
        val report = DatabaseIntegrityReport()

        Log.d(REPO_TAG, "Starting database integrity verification")

        val allTrainings = db.trainingDao().getAll()

        allTrainings.forEach { training ->
            // Check 1: Sequence not empty for saved trainings
            if (training.isDraft != true && training.sequence.isEmpty()) {
                report.addError("Training ${training.trainingId} (${training.name}) is saved but has empty sequence")
            }

            // Check 2: All workout IDs in sequence exist
            if (training.sequence.isNotEmpty()) {
                val workoutIds = training.sequence.split(",").mapNotNull { it.toLongOrNull() }
                val existingWorkouts = db.workoutDao().getByIds(workoutIds)
                val existingIds = existingWorkouts.map { it.workoutId }.toSet()

                val missingIds = workoutIds.filter { !existingIds.contains(it) }
                if (missingIds.isNotEmpty()) {
                    report.addError("Training ${training.trainingId} references missing workouts: $missingIds")
                }
            }

        }

        // Check 4: Find orphaned workouts
        val allWorkouts = db.workoutDao().getAll()
        allWorkouts.forEach { workout ->
            val isUsed = allTrainings.any { training ->
                training.sequence.split(",").mapNotNull { it.toLongOrNull() }.contains(workout.workoutId)
            }

            if (!isUsed) {
                report.addWarning("Orphaned workout found: id=${workout.workoutId}, name=${workout.name}")
            }
        }

        Log.d(REPO_TAG, "Integrity verification complete: ${report.errors.size} errors, ${report.warnings.size} warnings")
        return@withContext report
    }
}

/**
 * Report containing database integrity check results.
 */
data class DatabaseIntegrityReport(
    val errors: MutableList<String> = mutableListOf(),
    val warnings: MutableList<String> = mutableListOf()
) {
    fun addError(message: String) {
        errors.add(message)
        Log.e(REPO_TAG, "INTEGRITY ERROR: $message")
    }

    fun addWarning(message: String) {
        warnings.add(message)
        Log.w(REPO_TAG, "INTEGRITY WARNING: $message")
    }

    fun isHealthy() = errors.isEmpty()

    fun getSummary(): String {
        return "Database Integrity Report:\n" +
                "Errors: ${errors.size}\n" +
                "Warnings: ${warnings.size}\n" +
                if (errors.isNotEmpty()) "ERRORS:\n${errors.joinToString("\n")}\n" else "" +
                if (warnings.isNotEmpty()) "WARNINGS:\n${warnings.joinToString("\n")}" else ""
    }
}