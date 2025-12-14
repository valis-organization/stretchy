package com.example.stretchy.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Centralised helper for storage related permission checks & actions across API levels.
 */
class PermissionManager(private val context: Context) {
    // Legacy permissions (< Android 13)
    private val legacyWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val legacyRead = Manifest.permission.READ_EXTERNAL_STORAGE

    // Android 13+ granular media read permissions (use a subset appropriate for app data if stored as media)
    private val mediaImages = Manifest.permission.READ_MEDIA_IMAGES
    private val mediaVideo = Manifest.permission.READ_MEDIA_VIDEO
    private val mediaAudio = Manifest.permission.READ_MEDIA_AUDIO

    fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun hasLegacyWritePermission(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || hasPermission(legacyWrite)
    fun hasLegacyReadPermission(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true else hasPermission(legacyRead)

    fun mediaReadPermissions(): Array<String> = arrayOf(mediaImages, mediaVideo) // audio not needed for this app
    fun hasMediaReadPermissions(): Boolean =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) true else mediaReadPermissions().all { hasPermission(it) }

    fun needsManageAllFiles(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    fun hasManageAllFilesAccess(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()

    fun openManageAllFilesSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    }
}
