package com.example.stretchy.repository

import com.example.stretchy.database.data.ActivityType

data class Activity(
    var name: String,
    var activityOrder: Int = 0,
    var duration: Int,
    var activityType: ActivityType
) {
    var activityId: Long = 0
}