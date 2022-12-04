package com.example.stretchy.features.datatransport

import android.os.Environment

val dataTransportFilePath: String =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
const val dataTransportFileName: String = "StretchyTrainings.rafalczamp"