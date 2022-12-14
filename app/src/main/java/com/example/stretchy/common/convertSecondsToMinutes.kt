package com.example.stretchy.common

fun convertSecondsToMinutes(seconds: Long): String {
    val minutes = seconds / 60
    return "$minutes m ${seconds % 60}s"
}
