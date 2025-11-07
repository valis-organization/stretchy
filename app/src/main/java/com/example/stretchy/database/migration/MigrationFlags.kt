package com.example.stretchy.database.migration

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Migration flags manager for feature toggling during migration process
 * Allows gradual rollout and easy rollback if needed
 */
@Singleton
class MigrationFlags @Inject constructor(
    context: Context
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "migration_flags",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_DECOUPLED_BREAKS_ENABLED = "decoupled_breaks_enabled"
        private const val KEY_MIGRATION_COMPLETED = "migration_completed"
        private const val KEY_USE_NEW_NAVIGATION = "use_new_navigation"
        private const val KEY_USE_NEW_REPOSITORY = "use_new_repository"
        private const val KEY_MIGRATION_VERSION = "migration_version"
    }

    /**
     * Check if decoupled breaks system is enabled
     */
    var isDecoupledBreaksEnabled: Boolean
        get() = prefs.getBoolean(KEY_DECOUPLED_BREAKS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_DECOUPLED_BREAKS_ENABLED, value).apply()

    /**
     * Check if migration has been completed
     */
    var isMigrationCompleted: Boolean
        get() = prefs.getBoolean(KEY_MIGRATION_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_MIGRATION_COMPLETED, value).apply()

    /**
     * Check if we should use new navigation system
     */
    var useNewNavigation: Boolean
        get() = prefs.getBoolean(KEY_USE_NEW_NAVIGATION, false)
        set(value) = prefs.edit().putBoolean(KEY_USE_NEW_NAVIGATION, value).apply()

    /**
     * Check if we should use new repository
     */
    var useNewRepository: Boolean
        get() = prefs.getBoolean(KEY_USE_NEW_REPOSITORY, false)
        set(value) = prefs.edit().putBoolean(KEY_USE_NEW_REPOSITORY, value).apply()

    /**
     * Current migration version
     */
    var migrationVersion: Int
        get() = prefs.getInt(KEY_MIGRATION_VERSION, 1)
        set(value) = prefs.edit().putInt(KEY_MIGRATION_VERSION, value).apply()

    /**
     * Enable all new system features after successful migration
     */
    fun enableNewSystem() {
        prefs.edit()
            .putBoolean(KEY_DECOUPLED_BREAKS_ENABLED, true)
            .putBoolean(KEY_MIGRATION_COMPLETED, true)
            .putBoolean(KEY_USE_NEW_NAVIGATION, true)
            .putBoolean(KEY_USE_NEW_REPOSITORY, true)
            .putInt(KEY_MIGRATION_VERSION, 3)
            .apply()
    }

    /**
     * Rollback to old system (emergency fallback)
     */
    fun rollbackToOldSystem() {
        prefs.edit()
            .putBoolean(KEY_DECOUPLED_BREAKS_ENABLED, false)
            .putBoolean(KEY_USE_NEW_NAVIGATION, false)
            .putBoolean(KEY_USE_NEW_REPOSITORY, false)
            .putInt(KEY_MIGRATION_VERSION, 2)
            .apply()
    }

    /**
     * Reset all migration flags
     */
    fun resetAllFlags() {
        prefs.edit().clear().apply()
    }

    /**
     * Get migration status summary
     */
    fun getMigrationStatus(): MigrationStatus {
        return MigrationStatus(
            isDecoupledBreaksEnabled = isDecoupledBreaksEnabled,
            isMigrationCompleted = isMigrationCompleted,
            useNewNavigation = useNewNavigation,
            useNewRepository = useNewRepository,
            migrationVersion = migrationVersion
        )
    }
}

data class MigrationStatus(
    val isDecoupledBreaksEnabled: Boolean,
    val isMigrationCompleted: Boolean,
    val useNewNavigation: Boolean,
    val useNewRepository: Boolean,
    val migrationVersion: Int
) {
    val isFullyMigrated: Boolean
        get() = isDecoupledBreaksEnabled && isMigrationCompleted &&
                useNewNavigation && useNewRepository && migrationVersion >= 3
}
