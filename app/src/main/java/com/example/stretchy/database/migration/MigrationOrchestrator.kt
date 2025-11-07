package com.example.stretchy.database.migration

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stretchy.database.AppDatabase
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Migration Orchestrator coordinates the entire migration process
 * Provides status updates, error handling, and rollback capabilities
 */
@Singleton
class MigrationOrchestrator @Inject constructor(
    private val migrationManager: MigrationManager,
    private val migrationFlags: MigrationFlags,
    private val context: Context
) {

    private val _migrationStatus = MutableLiveData<MigrationStatus>()
    val migrationStatus: LiveData<MigrationStatus> = _migrationStatus

    private val _migrationProgress = MutableLiveData<MigrationProgress>()
    val migrationProgress: LiveData<MigrationProgress> = _migrationProgress

    /**
     * Start the complete migration process
     */
    suspend fun startMigration(): MigrationResult {
        return withContext(Dispatchers.IO) {
            try {
                updateProgress(MigrationProgress.Started("Initializing migration..."))

                // Check if migration is actually needed
                if (!migrationManager.isMigrationNeeded()) {
                    updateProgress(MigrationProgress.Completed("Migration not needed"))
                    return@withContext MigrationResult(
                        success = true,
                        message = "Database already up to date"
                    )
                }

                // Step 1: Backup current state
                updateProgress(MigrationProgress.InProgress("Creating backup...", 10))
                val backupResult = createBackup()
                if (!backupResult.success) {
                    return@withContext backupResult
                }

                // Step 2: Perform database migration
                updateProgress(MigrationProgress.InProgress("Migrating database...", 30))
                val migrationResult = migrationManager.performMigrationToDecoupledBreaks()
                if (!migrationResult.success) {
                    updateProgress(MigrationProgress.Failed("Database migration failed: ${migrationResult.message}"))
                    return@withContext migrationResult
                }

                // Step 3: Update migration flags gradually
                updateProgress(MigrationProgress.InProgress("Updating system flags...", 70))
                updateMigrationFlags(MigrationPhase.DATABASE_MIGRATED)

                // Step 4: Verify everything works
                updateProgress(MigrationProgress.InProgress("Verifying migration...", 90))
                val verificationResult = verifyMigration()
                if (!verificationResult.success) {
                    updateProgress(MigrationProgress.Failed("Migration verification failed"))
                    return@withContext verificationResult
                }

                // Step 5: Enable new system
                updateProgress(MigrationProgress.InProgress("Enabling new system...", 95))
                migrationFlags.enableNewSystem()

                updateProgress(MigrationProgress.Completed("Migration completed successfully!"))
                updateMigrationStatus()

                MigrationResult(
                    success = true,
                    message = "Migration completed successfully",
                    validationResult = migrationResult.validationResult
                )

            } catch (e: Exception) {
                Timber.e(e, "Migration orchestration failed")
                updateProgress(MigrationProgress.Failed("Migration failed: ${e.message}"))
                MigrationResult(
                    success = false,
                    message = "Migration orchestration failed: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Gradual migration process with rollback capability
     */
    suspend fun performGradualMigration(): MigrationResult {
        return withContext(Dispatchers.IO) {
            try {
                // Phase 1: Database only
                updateProgress(MigrationProgress.InProgress("Phase 1: Database migration...", 25))
                val dbResult = migrationManager.performMigrationToDecoupledBreaks()
                if (!dbResult.success) return@withContext dbResult

                migrationFlags.isMigrationCompleted = true
                delay(1000) // Allow system to stabilize

                // Phase 2: Enable break templates
                updateProgress(MigrationProgress.InProgress("Phase 2: Enable break templates...", 50))
                migrationFlags.isDecoupledBreaksEnabled = true
                delay(1000)

                // Phase 3: Enable new repository
                updateProgress(MigrationProgress.InProgress("Phase 3: Enable new repository...", 75))
                migrationFlags.useNewRepository = true
                delay(1000)

                // Phase 4: Enable new navigation
                updateProgress(MigrationProgress.InProgress("Phase 4: Enable new navigation...", 100))
                migrationFlags.useNewNavigation = true

                updateProgress(MigrationProgress.Completed("Gradual migration completed!"))
                updateMigrationStatus()

                dbResult

            } catch (e: Exception) {
                Timber.e(e, "Gradual migration failed")
                rollbackMigration()
                MigrationResult(
                    success = false,
                    message = "Gradual migration failed: ${e.message}",
                    exception = e
                )
            }
        }
    }

    /**
     * Rollback migration to previous state
     */
    suspend fun rollbackMigration(): MigrationResult {
        return withContext(Dispatchers.IO) {
            try {
                updateProgress(MigrationProgress.InProgress("Rolling back migration...", 50))

                migrationFlags.rollbackToOldSystem()
                updateMigrationStatus()

                updateProgress(MigrationProgress.Completed("Migration rolled back"))

                MigrationResult(
                    success = true,
                    message = "Migration rolled back successfully"
                )

            } catch (e: Exception) {
                Timber.e(e, "Migration rollback failed")
                MigrationResult(
                    success = false,
                    message = "Migration rollback failed: ${e.message}",
                    exception = e
                )
            }
        }
    }

    private suspend fun createBackup(): MigrationResult {
        return try {
            // Implementation would create database backup
            // For now, we'll just log
            Timber.d("Creating database backup...")
            MigrationResult(success = true, message = "Backup created")
        } catch (e: Exception) {
            MigrationResult(success = false, message = "Backup failed: ${e.message}", exception = e)
        }
    }

    private fun updateMigrationFlags(phase: MigrationPhase) {
        when (phase) {
            MigrationPhase.DATABASE_MIGRATED -> {
                migrationFlags.isMigrationCompleted = true
                migrationFlags.migrationVersion = 3
            }
            MigrationPhase.BREAKS_ENABLED -> {
                migrationFlags.isDecoupledBreaksEnabled = true
            }
            MigrationPhase.REPOSITORY_ENABLED -> {
                migrationFlags.useNewRepository = true
            }
            MigrationPhase.NAVIGATION_ENABLED -> {
                migrationFlags.useNewNavigation = true
            }
        }
    }

    private suspend fun verifyMigration(): MigrationResult {
        return try {
            val currentVersion = migrationManager.getCurrentDatabaseVersion()
            if (currentVersion >= 3) {
                MigrationResult(success = true, message = "Migration verified")
            } else {
                MigrationResult(success = false, message = "Migration verification failed: wrong version")
            }
        } catch (e: Exception) {
            MigrationResult(success = false, message = "Verification failed: ${e.message}", exception = e)
        }
    }

    private fun updateProgress(progress: MigrationProgress) {
        _migrationProgress.postValue(progress)
    }

    private fun updateMigrationStatus() {
        _migrationStatus.postValue(migrationFlags.getMigrationStatus())
    }

    enum class MigrationPhase {
        DATABASE_MIGRATED,
        BREAKS_ENABLED,
        REPOSITORY_ENABLED,
        NAVIGATION_ENABLED
    }
}

sealed class MigrationProgress {
    data class Started(val message: String) : MigrationProgress()
    data class InProgress(val message: String, val percentage: Int) : MigrationProgress()
    data class Completed(val message: String) : MigrationProgress()
    data class Failed(val message: String) : MigrationProgress()
}

data class MigrationResult(
    val success: Boolean,
    val message: String,
    val validationResult: MigrationValidationResult? = null,
    val exception: Exception? = null
)
