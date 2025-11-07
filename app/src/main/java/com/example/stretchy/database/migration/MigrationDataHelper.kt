package com.example.stretchy.database.migration

import android.content.Context
import com.example.stretchy.database.AppDatabase
import com.example.stretchy.database.dao.BreakTemplateDao
import com.example.stretchy.database.entity.BreakTemplateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Migration helper for safe database transition
 * Provides utilities for pre-migration backup and post-migration validation
 */
class MigrationDataHelper(private val context: Context) {

    /**
     * Data class for pre-migration backup
     */
    data class PreMigrationSnapshot(
        val trainings: List<TrainingSnapshot>,
        val totalActivities: Int,
        val totalBreaks: Int
    )

    data class TrainingSnapshot(
        val trainingId: Long,
        val trainingName: String,
        val activities: List<ActivitySnapshot>
    )

    data class ActivitySnapshot(
        val activityId: Long,
        val name: String,
        val duration: Int,
        val activityType: String,
        val order: Int
    )

    /**
     * Create snapshot of current data before migration
     */
    suspend fun createPreMigrationSnapshot(): PreMigrationSnapshot = withContext(Dispatchers.IO) {
        // This would be called before migration to backup current state
        // For now, we'll create a simple implementation

        // Use raw SQLiteDatabase to avoid Room schema validation which may attempt to migrate
        val db = context.openOrCreateDatabase(AppDatabase.NAME, Context.MODE_PRIVATE, null)

        try {
            val trainings = mutableListOf<TrainingSnapshot>()
            var totalActivities = 0
            var totalBreaks = 0

            // Query all trainings
            val trainingCursor = db.rawQuery("SELECT trainingId, name FROM training", null)
            if (trainingCursor.moveToFirst()) {
                do {
                    val trainingId = trainingCursor.getLong(0)
                    val trainingName = trainingCursor.getString(1)

                    // Get activities for this training ordered by activityOrder
                    val activities = mutableListOf<ActivitySnapshot>()
                    val activityCursor = db.rawQuery(
                        """
                        SELECT a.activityId, a.name, a.duration, a.activityType, ta.activityOrder
                        FROM training_activities ta
                        INNER JOIN activity a ON ta.aId = a.activityId
                        WHERE ta.tId = ?
                        ORDER BY ta.activityOrder
                    """, arrayOf(trainingId.toString())
                    )

                    if (activityCursor.moveToFirst()) {
                        do {
                            val activityId = activityCursor.getLong(0)
                            val name = activityCursor.getString(1)
                            val duration = activityCursor.getInt(2)
                            val activityType = activityCursor.getString(3)
                            val order = activityCursor.getInt(4)

                            totalActivities++
                            if (activityType == "BREAK") totalBreaks++

                            activities.add(
                                ActivitySnapshot(
                                    activityId = activityId,
                                    name = name,
                                    duration = duration,
                                    activityType = activityType,
                                    order = order
                                )
                            )
                        } while (activityCursor.moveToNext())
                    }
                    activityCursor.close()

                    trainings.add(TrainingSnapshot(
                        trainingId = trainingId,
                        trainingName = trainingName,
                        activities = activities
                    ))

                } while (trainingCursor.moveToNext())
            }
            trainingCursor.close()

            PreMigrationSnapshot(trainings, totalActivities, totalBreaks)
        } finally {
            db.close()
        }
    }

    /**
     * Validate migration results against pre-migration snapshot
     */
    suspend fun validateMigrationResults(
        preSnapshot: PreMigrationSnapshot,
        newDatabase: AppDatabase
    ): MigrationValidationResult = withContext(Dispatchers.IO) {

        try {
            val errors = mutableListOf<String>()

            // Check trainings count
            val postTrainingCount = newDatabase.trainingDao().getAll().size
            val preTrainingCount = preSnapshot.trainings.size

            if (postTrainingCount != preTrainingCount) {
                errors.add("Training count mismatch: expected $preTrainingCount, got $postTrainingCount")
            }

            // Check break templates were created
            val breakTemplateDao = newDatabase.breakTemplateDao()
            val breakTemplates = breakTemplateDao.getAllBreakTemplates()

            // Verify each training's sequence integrity
            val trainingSequenceDao = newDatabase.trainingSequenceDao()
            var totalSequenceItems = 0
            var totalExercisesInSequences = 0

            preSnapshot.trainings.forEach { training ->
                val sequences = trainingSequenceDao.getTrainingSequenceWithDetails(training.trainingId)
                totalSequenceItems += sequences.size

                // Count exercises (non-break activities) in original training
                val originalExercises = training.activities.filter { it.activityType != "BREAK" }
                totalExercisesInSequences += sequences.size // Each sequence item should be an exercise

                // Verify sequence order matches original order
                val originalOrder = training.activities
                    .filter { it.activityType != "BREAK" }
                    .sortedBy { it.order }
                    .map { it.activityId }

                val sequenceOrder = sequences.sortedBy { it.sequenceOrder }
                    .map { it.exerciseId }

                if (originalOrder != sequenceOrder) {
                    errors.add("Exercise order mismatch in training ${training.trainingId}: expected $originalOrder, got $sequenceOrder")
                }

                // Verify exercise count per training
                if (sequences.size != originalExercises.size) {
                    errors.add("Exercise count mismatch in training ${training.trainingId}: expected ${originalExercises.size}, got ${sequences.size}")
                }
            }

            // Calculate expected counts
            val expectedExerciseCount = preSnapshot.trainings.sumOf { training ->
                training.activities.count { it.activityType != "BREAK" }
            }

            val expectedBreakCount = preSnapshot.trainings.sumOf { training ->
                training.activities.count { it.activityType == "BREAK" }
            }

            // Validate break templates cover all unique break durations
            val originalBreakDurations = preSnapshot.trainings.flatMap { training ->
                training.activities.filter { it.activityType == "BREAK" }.map { it.duration }
            }.distinct()

            val templateDurations = breakTemplates.map { it.duration }
            val missingBreakDurations = originalBreakDurations.filter { it !in templateDurations }

            if (missingBreakDurations.isNotEmpty()) {
                errors.add("Missing break templates for durations: $missingBreakDurations")
            }

            // Verify total exercise count integrity
            if (totalExercisesInSequences != expectedExerciseCount) {
                errors.add("Total exercise count mismatch: expected $expectedExerciseCount, got $totalExercisesInSequences")
            }

            // Verify break templates existence for original breaks
            if (expectedBreakCount > 0 && breakTemplates.isEmpty()) {
                errors.add("Expected break templates but none were created (original had $expectedBreakCount breaks)")
            }

            // Final validation
            val migrationSuccess = errors.isEmpty() &&
                                  totalExercisesInSequences == expectedExerciseCount &&
                                  (expectedBreakCount == 0 || breakTemplates.isNotEmpty())

            MigrationValidationResult(
                success = migrationSuccess,
                preTrainingCount = preTrainingCount,
                postTrainingCount = postTrainingCount,
                preActivityCount = preSnapshot.totalActivities,
                postSequenceCount = totalSequenceItems,
                breakTemplatesCreated = breakTemplates.size,
                errors = errors
            )
        } catch (e: Exception) {
            MigrationValidationResult(
                success = false,
                preTrainingCount = preSnapshot.trainings.size,
                postTrainingCount = 0,
                preActivityCount = preSnapshot.totalActivities,
                postSequenceCount = 0,
                breakTemplatesCreated = 0,
                errors = listOf("Migration validation failed: ${e.message}")
            )
        }
    }

    /**
     * Validate pre-migration data integrity
     */
    suspend fun validatePreMigrationData(preSnapshot: PreMigrationSnapshot): List<String> = withContext(Dispatchers.IO) {
        val issues = mutableListOf<String>()

        // Check for orphaned activities (activities without training reference)
        preSnapshot.trainings.forEach { training ->
            if (training.activities.isEmpty()) {
                issues.add("Training ${training.trainingId} has no activities")
            }

            // Check for duplicate order values within training
            val orders = training.activities.map { it.order }
            val duplicateOrders = orders.groupBy { it }.filter { it.value.size > 1 }.keys
            if (duplicateOrders.isNotEmpty()) {
                issues.add("Training ${training.trainingId} has duplicate activity orders: $duplicateOrders")
            }

            // Check for activities with invalid durations
            training.activities.forEach { activity ->
                if (activity.duration <= 0) {
                    issues.add("Activity ${activity.activityId} in training ${training.trainingId} has invalid duration: ${activity.duration}")
                }
            }
        }

        // Check total counts consistency
        val calculatedActivities = preSnapshot.trainings.sumOf { it.activities.size }
        if (calculatedActivities != preSnapshot.totalActivities) {
            issues.add("Activity count mismatch: calculated $calculatedActivities vs snapshot ${preSnapshot.totalActivities}")
        }

        val calculatedBreaks = preSnapshot.trainings.sumOf { training ->
            training.activities.count { it.activityType == "BREAK" }
        }
        if (calculatedBreaks != preSnapshot.totalBreaks) {
            issues.add("Break count mismatch: calculated $calculatedBreaks vs snapshot ${preSnapshot.totalBreaks}")
        }

        issues
    }

    /**
     * Initialize break templates for all unique break durations from migration
     */
    suspend fun initializeBreakTemplatesFromSnapshot(
        breakTemplateDao: BreakTemplateDao,
        preSnapshot: PreMigrationSnapshot
    ) = withContext(Dispatchers.IO) {

        // Get all unique break durations from the snapshot
        val breakDurations = preSnapshot.trainings.flatMap { training ->
            training.activities.filter { it.activityType == "BREAK" }.map { it.duration }
        }.distinct()

        // Create templates for each unique duration found in migration data
        breakDurations.forEach { duration ->
            val existing = breakTemplateDao.findBreakByDuration(duration)
            if (existing == null) {
                val breakTemplate = BreakTemplateEntity(
                    breakId = "migrated_break_${duration}s",
                    duration = duration,
                    usageCount = 1 // Mark as used since it came from existing data
                )
                breakTemplateDao.insertBreakTemplate(breakTemplate)
            }
        }

        // Also add common durations if not already present
        initializeCommonBreakTemplates(breakTemplateDao)
    }

    /**
     * Initialize common break templates after migration
     */
    suspend fun initializeCommonBreakTemplates(breakTemplateDao: BreakTemplateDao) = withContext(Dispatchers.IO) {
        val commonDurations = listOf(5, 10, 15, 30, 45, 60, 90, 120)

        commonDurations.forEach { duration ->
            val existing = breakTemplateDao.findBreakByDuration(duration)
            if (existing == null) {
                val breakTemplate = BreakTemplateEntity(
                    breakId = "common_break_${duration}s",
                    duration = duration,
                    usageCount = 0
                )
                breakTemplateDao.insertBreakTemplate(breakTemplate)
            }
        }
    }
}

data class MigrationValidationResult(
    val success: Boolean,
    val preTrainingCount: Int,
    val postTrainingCount: Int,
    val preActivityCount: Int,
    val postSequenceCount: Int,
    val breakTemplatesCreated: Int,
    val errors: List<String>
)
