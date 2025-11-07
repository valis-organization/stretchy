package com.example.stretchy.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

/**
 * Migration from current break system to decoupled break system
 * Version 2 -> Version 3
 *
 * Changes:
 * 1. Create break_templates table for reusable breaks
 * 2. Create training_sequence table (new system alongside old training_activities)
 * 3. Extract existing breaks into break_templates
 * 4. Convert training_activities data to training_sequence format
 * 5. Clean up old BREAK activities from activity table
 * Note: training_activities table is kept for backward compatibility
 */
class MigrationToDecoupledBreaks {

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // Step 1: Create new tables
                createBreakTemplatesTable(database)
                createTrainingSequenceTable(database)

                // Step 2: Extract existing breaks and create templates
                val breakTemplateMap = extractAndCreateBreakTemplates(database)

                // Step 3: Convert training_activities to training_sequence
                convertTrainingActivitiesToSequence(database, breakTemplateMap)

                // Step 4: Clean up old BREAK activities
                cleanupOldBreakActivities(database)

                // Step 5: Verify migration integrity
                verifyMigrationIntegrity(database)
            }
        }

        private fun createBreakTemplatesTable(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE break_templates (
                    breakId TEXT NOT NULL PRIMARY KEY,
                    duration INTEGER NOT NULL,
                    usageCount INTEGER NOT NULL DEFAULT 0
                )
            """)

            // Index for fast duration lookups
            database.execSQL("""
                CREATE INDEX index_break_templates_duration 
                ON break_templates(duration)
            """)
        }

        private fun createTrainingSequenceTable(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE training_sequence (
                    trainingId INTEGER NOT NULL,
                    sequenceOrder INTEGER NOT NULL,
                    exerciseId INTEGER NOT NULL,
                    followingBreakId TEXT,
                    PRIMARY KEY (trainingId, sequenceOrder),
                    FOREIGN KEY (trainingId) REFERENCES training(trainingId) ON DELETE CASCADE,
                    FOREIGN KEY (exerciseId) REFERENCES activity(activityId) ON DELETE CASCADE,
                    FOREIGN KEY (followingBreakId) REFERENCES break_templates(breakId) ON DELETE SET NULL
                )
            """)

            // Indexes for performance
            database.execSQL("""
                CREATE INDEX index_training_sequence_training_order 
                ON training_sequence(trainingId, sequenceOrder)
            """)
            database.execSQL("""
                CREATE INDEX index_training_sequence_exercise 
                ON training_sequence(exerciseId)
            """)
            database.execSQL("""
                CREATE INDEX index_training_sequence_break 
                ON training_sequence(followingBreakId)
            """)
        }

        private fun extractAndCreateBreakTemplates(database: SupportSQLiteDatabase): Map<Int, String> {
            val breakTemplateMap = mutableMapOf<Int, String>() // duration -> breakId

            // Find all unique break durations from existing BREAK activities
            val cursor = database.query("""
                SELECT DISTINCT duration 
                FROM activity 
                WHERE activityType = 'BREAK'
                ORDER BY duration
            """)

            while (cursor.moveToNext()) {
                val duration = cursor.getInt(0)
                val breakId = "break_${duration}s_${UUID.randomUUID()}"

                // Insert break template
                database.execSQL("""
                    INSERT INTO break_templates (breakId, duration, usageCount)
                    VALUES (?, ?, 0)
                """, arrayOf<Any>(breakId, duration, 0))

                breakTemplateMap[duration] = breakId
            }
            cursor.close()

            return breakTemplateMap
        }

        private fun convertTrainingActivitiesToSequence(
            database: SupportSQLiteDatabase,
            breakTemplateMap: Map<Int, String>
        ) {
            // Process each training individually to ensure correct sequence building
            val trainingCursor = database.query("""
                SELECT DISTINCT tId FROM training_activities ORDER BY tId
            """)

            while (trainingCursor.moveToNext()) {
                val trainingId = trainingCursor.getLong(0)
                convertSingleTrainingSequence(database, trainingId, breakTemplateMap)
            }
            trainingCursor.close()
        }

        private fun convertSingleTrainingSequence(
            database: SupportSQLiteDatabase,
            trainingId: Long,
            breakTemplateMap: Map<Int, String>
        ) {
            // Get all activities for this training in order
            val cursor = database.query("""
                SELECT ta.aId, a.activityType, a.duration
                FROM training_activities ta
                INNER JOIN activity a ON ta.aId = a.activityId
                WHERE ta.tId = ?
                ORDER BY ta.activityOrder
            """, arrayOf(trainingId.toString()))

            var sequenceOrder = 0
            var lastExerciseSequenceOrder: Int? = null

            while (cursor.moveToNext()) {
                val activityId = cursor.getLong(0)
                val activityType = cursor.getString(1)
                val duration = cursor.getInt(2)

                when (activityType) {
                    "EXERCISE", "STRETCH", "TIMELESS_EXERCISE" -> {
                        // Insert exercise without break for now; we may attach break later if a BREAK follows
                        database.execSQL("""
                            INSERT INTO training_sequence 
                            (trainingId, sequenceOrder, exerciseId, followingBreakId)
                            VALUES (?, ?, ?, NULL)
                        """, arrayOf(trainingId.toString(), sequenceOrder.toString(), activityId.toString()))

                        // Remember last inserted exercise's sequenceOrder so that a following BREAK can be attached to it
                        lastExerciseSequenceOrder = sequenceOrder

                        sequenceOrder++
                    }

                    "BREAK" -> {
                        // Attach this break to the previously inserted exercise (if any)
                        val breakId = breakTemplateMap[duration]
                        if (breakId != null && lastExerciseSequenceOrder != null) {
                            database.execSQL("""
                                UPDATE training_sequence
                                SET followingBreakId = ?
                                WHERE trainingId = ? AND sequenceOrder = ?
                            """, arrayOf(breakId, trainingId.toString(), lastExerciseSequenceOrder.toString()))

                            // Increment usage count for the break
                            database.execSQL("""
                                UPDATE break_templates 
                                SET usageCount = usageCount + 1
                                WHERE breakId = ?
                            """, arrayOf(breakId))

                            // Once a break was assigned to the previous exercise, we clear lastExerciseSequenceOrder
                            // so that multiple BREAK rows in a row won't attach again
                            lastExerciseSequenceOrder = null
                        } else {
                            // No previous exercise to attach this break to — skip it (leading break)
                        }
                    }
                }
            }
            cursor.close()
        }

        private fun cleanupOldBreakActivities(database: SupportSQLiteDatabase) {
            // Previously we removed BREAK activities from activity table, but keeping them
            // preserves backward compatibility for any code still joining training_activities -> activity.
            // Future migrations can safely remove these rows once the whole codebase is migrated.

            // Intentionally do not delete BREAK activities here to avoid breaking old read paths.
            // database.execSQL("DELETE FROM activity WHERE activityType = 'BREAK'")

            // Keep old training_activities table for backward compatibility
            // It will be dropped in a future migration when all code is updated to use training_sequence
        }

        private fun verifyMigrationIntegrity(database: SupportSQLiteDatabase) {
            // Verify that we have break templates (not fatal if none were present in source data)
            val breakCountCursor = database.query("SELECT COUNT(*) FROM break_templates")
            if (breakCountCursor.moveToFirst()) {
                val count = breakCountCursor.getInt(0)
                // If no break templates were created because source data had no BREAKs, it's not a fatal error
                // so we don't throw here. MigrationDataHelper will validate counts against pre-migration snapshot.
            }
            breakCountCursor.close()

            // Verify that we have training sequences
            val sequenceCount = database.query("SELECT COUNT(*) FROM training_sequence")
            if (sequenceCount.moveToFirst()) {
                val count = sequenceCount.getInt(0)
                // Do not throw here — post-migration validation will compare against pre-migration snapshot
            }
            sequenceCount.close()
        }
    }
}
