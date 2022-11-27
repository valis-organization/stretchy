package com.example.stretchy.repository

import com.example.stretchy.database.data.TrainingType

class TrainingWithActivity(
    val trainingId: Long = 0,
    val name: String,
    val trainingType: TrainingType,
    val finished: Boolean,
    val activities: List<Activity>
)