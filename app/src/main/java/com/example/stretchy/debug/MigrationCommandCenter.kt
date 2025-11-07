package com.example.stretchy.debug

import com.example.stretchy.database.migration.*
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Migration Command Center for debugging and manual migration control
 * Provides admin interface for migration management
 */
@Singleton
class MigrationCommandCenter @Inject constructor(
    private val migrationOrchestrator: MigrationOrchestrator,
    private val migrationManager: MigrationManager,
    private val migrationFlags: MigrationFlags
) {

    /**
     * Execute migration command
     */
    suspend fun executeCommand(command: MigrationCommand): CommandResult {
        return withContext(Dispatchers.IO) {
            try {
                when (command) {
                    MigrationCommand.STATUS -> getStatus()
                    MigrationCommand.TEST -> runTests()
                    MigrationCommand.MIGRATE -> performMigration()
                    MigrationCommand.MIGRATE_GRADUAL -> performGradualMigration()
                    MigrationCommand.ROLLBACK -> rollbackMigration()
                    MigrationCommand.RESET_FLAGS -> resetFlags()
                    MigrationCommand.ENABLE_NEW_SYSTEM -> enableNewSystem()
                    MigrationCommand.DISABLE_NEW_SYSTEM -> disableNewSystem()
                }
            } catch (e: Exception) {
                CommandResult.Error("Command execution failed: ${e.message}")
            }
        }
    }

    private fun getStatus(): CommandResult {
        val status = migrationFlags.getMigrationStatus()
        val dbVersion = migrationManager.getCurrentDatabaseVersion()
        val needsMigration = migrationManager.isMigrationNeeded()

        return CommandResult.Success("""
            📊 Migration Status:
            
            🗄️ Database Version: $dbVersion
            🔄 Needs Migration: $needsMigration
            
            🚩 Feature Flags:
            • Decoupled Breaks: ${status.isDecoupledBreaksEnabled}
            • Migration Completed: ${status.isMigrationCompleted}
            • New Navigation: ${status.useNewNavigation}
            • New Repository: ${status.useNewRepository}
            • Migration Version: ${status.migrationVersion}
            
            🎯 Overall Status: ${if (status.isFullyMigrated) "✅ Fully Migrated" else "⏳ Migration Pending"}
        """.trimIndent())
    }

    private suspend fun runTests(): CommandResult {
        val testSuite = MigrationTestSuite(migrationOrchestrator, migrationManager)
        val results = testSuite.runAllTests()

        val passCount = results.count { it.success }
        val totalCount = results.size

        val summary = results.joinToString("\n") { result ->
            val status = if (result.success) "✅" else "❌"
            "$status ${result.testName}: ${result.message}"
        }

        return CommandResult.Success("""
            🧪 Test Results ($passCount/$totalCount passed):
            
            $summary
            
            ${if (passCount == totalCount) "🎉 Ready for migration!" else "⚠️ Fix issues before migration"}
        """.trimIndent())
    }

    private suspend fun performMigration(): CommandResult {
        val result = migrationOrchestrator.startMigration()
        return if (result.success) {
            CommandResult.Success("🎉 Migration completed successfully!\n${result.message}")
        } else {
            CommandResult.Error("❌ Migration failed: ${result.message}")
        }
    }

    private suspend fun performGradualMigration(): CommandResult {
        val result = migrationOrchestrator.performGradualMigration()
        return if (result.success) {
            CommandResult.Success("🎉 Gradual migration completed!\n${result.message}")
        } else {
            CommandResult.Error("❌ Gradual migration failed: ${result.message}")
        }
    }

    private suspend fun rollbackMigration(): CommandResult {
        val result = migrationOrchestrator.rollbackMigration()
        return if (result.success) {
            CommandResult.Success("↩️ Migration rolled back successfully!\n${result.message}")
        } else {
            CommandResult.Error("❌ Rollback failed: ${result.message}")
        }
    }

    private fun resetFlags(): CommandResult {
        migrationFlags.resetAllFlags()
        return CommandResult.Success("🔄 All migration flags reset to default state")
    }

    private fun enableNewSystem(): CommandResult {
        migrationFlags.enableNewSystem()
        return CommandResult.Success("✅ New system enabled (all flags set)")
    }

    private fun disableNewSystem(): CommandResult {
        migrationFlags.rollbackToOldSystem()
        return CommandResult.Success("⬅️ Rolled back to old system (new flags disabled)")
    }

    /**
     * Get available commands help
     */
    fun getHelp(): String {
        return """
            🛠️ Migration Command Center
            
            Available Commands:
            
            📊 STATUS - Show current migration status
            🧪 TEST - Run migration test suite
            🚀 MIGRATE - Perform full migration
            ⏳ MIGRATE_GRADUAL - Perform gradual migration
            ↩️ ROLLBACK - Rollback migration
            🔄 RESET_FLAGS - Reset all migration flags
            ✅ ENABLE_NEW_SYSTEM - Force enable new system
            ❌ DISABLE_NEW_SYSTEM - Force disable new system
            
            Usage: Call executeCommand(MigrationCommand.*)
        """.trimIndent()
    }
}

enum class MigrationCommand {
    STATUS,
    TEST,
    MIGRATE,
    MIGRATE_GRADUAL,
    ROLLBACK,
    RESET_FLAGS,
    ENABLE_NEW_SYSTEM,
    DISABLE_NEW_SYSTEM
}

sealed class CommandResult {
    data class Success(val message: String) : CommandResult()
    data class Error(val message: String) : CommandResult()
}
