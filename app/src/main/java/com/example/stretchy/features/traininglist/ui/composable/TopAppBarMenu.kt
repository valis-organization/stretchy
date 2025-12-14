package com.example.stretchy.features.traininglist.ui.composable

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.stretchy.R

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun Menu(
    onRequestExportPermission: () -> Unit,
    onRequestImportPermission: () -> Unit,
    onPerformExport: () -> Unit,
    onPerformImport: suspend () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember {
        mutableStateOf(false)
    }
    IconButton(
        onClick = {
            expanded = true
        }
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(id = R.string.desc_menu_icon),
            tint = Color.Black
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    if (isPermissionsGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        CoroutineScope(Dispatchers.Default).launch {
                            onPerformImport()
                        }
                    } else {
                        onRequestImportPermission()
                    }
                    Toast.makeText(context, R.string.import_trainings, Toast.LENGTH_LONG).show()
                }
            ) {
                Text(text = stringResource(id = R.string.import_trainings))
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    if (isPermissionsGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        onPerformExport()
                    } else {
                        onRequestExportPermission()
                    }
                    Toast.makeText(context, R.string.export_trainings, Toast.LENGTH_LONG).show()
                }
            ) {
                Text(text = stringResource(id = R.string.export_trainings))
            }
        }
    }
}

private fun isPermissionsGranted(context: Context, permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val result =
            ContextCompat.checkSelfPermission(context, permission)
        result == PackageManager.PERMISSION_GRANTED
    }
}
