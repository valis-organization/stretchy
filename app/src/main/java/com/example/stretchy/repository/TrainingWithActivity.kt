package com.example.stretchy.repository

import com.example.stretchy.database.data.TrainingType

class TrainingWithActivity(
    val name: String,
    val trainingType: TrainingType,
    val finished: Boolean,
    val activities: List<Activity>
){
    var id: Long = 0
}