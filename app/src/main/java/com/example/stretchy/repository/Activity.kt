package com.example.stretchy.repository

import com.example.stretchy.database.data.ActivityType

class Activity(
    val name: String,
    val duration: Int,
    val activityType: ActivityType
) {
    var activityId: Long = 0
}