package com.example.stretchy.permission

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

/**
 * Compose-friendly storage permission abstraction handling API differences.
 * - < Android 13: READ/WRITE_EXTERNAL_STORAGE (WRITE only needed < Android 10 typically)
 * - Android 11+: MANAGE_EXTERNAL_STORAGE optional (user navigated to settings)
 * - Android 13+: READ_MEDIA_* granular permissions (we treat READ as IMAGES + VIDEO)
 */
@Stable
class StoragePermissionState internal constructor(
    private val permissionManager: PermissionManager,
    private val requestLegacyWrite: () -> Unit,
    private val requestLegacyRead: () -> Unit,
    private val requestMediaPermissions: () -> Unit,
) {
    val hasManageAllFilesAccess: Boolean get() = permissionManager.hasManageAllFilesAccess()
    val hasLegacyWrite: Boolean get() = permissionManager.hasLegacyWritePermission()
    val hasLegacyRead: Boolean get() = permissionManager.hasLegacyReadPermission()
    val hasMediaRead: Boolean get() = permissionManager.hasMediaReadPermissions()

    /** True if app can perform write/export given current API level. */
    val canWrite: Boolean
        get() = when {
            permissionManager.needsManageAllFiles() -> hasManageAllFilesAccess
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> hasLegacyWrite
            else -> true // Scoped storage write via SAF not gated by WRITE_EXTERNAL_STORAGE
        }

    /** True if app can perform read/import given current API level. */
    val canRead: Boolean
        get() = when {
            permissionManager.needsManageAllFiles() -> hasManageAllFilesAccess
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> hasMediaRead
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> hasLegacyRead
            else -> true
        }

    /** Derived convenience flags for UI enabling/disabling actions. */
    val isExportReady: Boolean get() = canWrite
    val isImportReady: Boolean get() = canRead

    /** Request write/export capability. */
    fun requestWrite() {
        when {
            permissionManager.needsManageAllFiles() && !hasManageAllFilesAccess -> permissionManager.openManageAllFilesSettings()
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !hasLegacyWrite -> requestLegacyWrite() // WRITE only meaningful pre-Android 10
            else -> { /* no-op */ }
        }
    }

    /** Request read/import capability depending on API. */
    fun requestRead() {
        when {
            permissionManager.needsManageAllFiles() && !hasManageAllFilesAccess -> permissionManager.openManageAllFilesSettings()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasMediaRead -> requestMediaPermissions()
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !hasLegacyRead -> requestLegacyRead()
            else -> { /* already granted */ }
        }
    }
}

@Composable
fun rememberStoragePermissionState(permissionManager: PermissionManager): StoragePermissionState {
    // Backing state values updated by launcher callbacks
    var legacyWriteGranted by remember { mutableStateOf(permissionManager.hasLegacyWritePermission()) }
    var legacyReadGranted by remember { mutableStateOf(permissionManager.hasLegacyReadPermission()) }
    var mediaGranted by remember { mutableStateOf(permissionManager.hasMediaReadPermissions()) }

    val writeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        legacyWriteGranted = granted
    }
    val readLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        legacyReadGranted = granted
    }
    val mediaMultiLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        // Granted only if all requested permissions granted (images & video)
        mediaGranted = result.all { it.value }
    }

    val requestWrite: () -> Unit = {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !legacyWriteGranted) {
            writeLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !permissionManager.hasManageAllFilesAccess()) {
            permissionManager.openManageAllFilesSettings()
        }
    }

    val requestRead: () -> Unit = {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (!mediaGranted) mediaMultiLauncher.launch(permissionManager.mediaReadPermissions())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                if (!permissionManager.hasManageAllFilesAccess()) permissionManager.openManageAllFilesSettings() else mediaGranted = true
            }
            else -> {
                if (!legacyReadGranted) readLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    val requestMedia: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !mediaGranted) {
            mediaMultiLauncher.launch(permissionManager.mediaReadPermissions())
        }
    }

    return remember(permissionManager, legacyWriteGranted, legacyReadGranted, mediaGranted) {
        StoragePermissionState(
            permissionManager = permissionManager,
            requestLegacyWrite = requestWrite,
            requestLegacyRead = requestRead,
            requestMediaPermissions = requestMedia
        )
    }
}
