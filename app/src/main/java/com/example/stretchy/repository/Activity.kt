package com.example.stretchy.repository

import com.example.stretchy.database.data.ActivityType

class Activity(
    val activityId: Long = 0,
    val name: String,
    val duration: Int,
    val activityType: ActivityType
)