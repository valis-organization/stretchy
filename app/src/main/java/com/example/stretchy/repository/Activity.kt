package com.example.stretchy.repository

import com.example.stretchy.database.data.ActivityType

class Activity(
    var name: String,
    var duration: Int,
    var activityType: ActivityType
) {
    var activityId: Long = 0
}