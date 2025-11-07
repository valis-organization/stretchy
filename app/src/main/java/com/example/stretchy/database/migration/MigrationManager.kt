package com.example.stretchy.database.migration

import android.content.Context
import androidx.room.Room
import com.example.stretchy.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Migration Manager handles the complete migration process
 * Provides safe migration with rollback capabilities and validation
 */
@Singleton
class MigrationManager @Inject constructor(
    private val context: Context
) {

    data class MigrationResult(
        val success: Boolean,
        val message: String,
        val validationResult: MigrationValidationResult? = null,
        val exception: Exception? = null
    )

    /**
     * Perform complete migration to decoupled break system
     */
    suspend fun performMigrationToDecoupledBreaks(): MigrationResult = withContext(Dispatchers.IO) {

        val migrationHelper = MigrationDataHelper(context)

        try {
            Timber.d("Starting migration to decoupled breaks system...")

            // Step 1: Create pre-migration snapshot
            Timber.d("Creating pre-migration snapshot...")
            val preSnapshot = migrationHelper.createPreMigrationSnapshot()

            // Step 2: Perform database migration
            Timber.d("Performing database migration...")
            val newDatabase = createMigratedDatabase()

            // Step 3: Validate migration results
            Timber.d("Validating migration results...")
            val validationResult = migrationHelper.validateMigrationResults(preSnapshot, newDatabase)

            if (!validationResult.success) {
                return@withContext MigrationResult(
                    success = false,
                    message = "Migration validation failed: ${validationResult.errors.joinToString()}",
                    validationResult = validationResult
                )
            }

            // Step 4: Initialize common break templates
            Timber.d("Initializing common break templates...")
            migrationHelper.initializeCommonBreakTemplates(newDatabase.breakTemplateDao())

            // Step 5: Clean up and finalize
            newDatabase.close()

            Timber.d("Migration completed successfully!")
            MigrationResult(
                success = true,
                message = "Migration completed successfully. " +
                        "Migrated ${validationResult.preTrainingCount} trainings, " +
                        "created ${validationResult.breakTemplatesCreated} break templates.",
                validationResult = validationResult
            )

        } catch (e: Exception) {
            Timber.e(e, "Migration failed")
            MigrationResult(
                success = false,
                message = "Migration failed: ${e.message}",
                exception = e
            )
        }
    }

    /**
     * Create database instance with migrations applied
     */
    private fun createMigratedDatabase(): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.NAME
        )
        .addMigrations(
            AppDatabase.MIGRATION_1_2,
            AppDatabase.MIGRATION_2_3
        )
        .fallbackToDestructiveMigration() // Only as last resort
        .build()
    }

    /**
     * Check if migration is needed
     */
    fun isMigrationNeeded(): Boolean {
        return try {
            val database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.NAME
            ).build()

            // Check if new tables exist
            val cursor = database.openHelper.readableDatabase.query(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='break_templates'"
            )
            val hasNewTables = cursor.count > 0
            cursor.close()
            database.close()

            !hasNewTables
        } catch (e: Exception) {
            Timber.w(e, "Could not check migration status")
            true // Assume migration is needed if we can't check
        }
    }

    /**
     * Get current database version
     */
    fun getCurrentDatabaseVersion(): Int {
        return try {
            val database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.NAME
            ).build()

            val version = database.openHelper.readableDatabase.version
            database.close()
            version
        } catch (e: Exception) {
            Timber.w(e, "Could not get database version")
            1 // Default to version 1
        }
    }
}
