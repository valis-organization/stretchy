package com.example.stretchy.features.datatransport

import android.os.Environment

fun dataTransportFilePath(): String =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
fun dataTransportFileName(): String = "StretchyTrainings.rafalczamp"