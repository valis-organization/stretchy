package com.example.stretchy.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TrainingWithActivitiesEntity (
    @Embedded
    val training : TrainingEntity,
    @Relation(
        parentColumn = "trainingId",
        entity = ActivityEntity::class,
        entityColumn = "activityId",
        associateBy = Junction(
            value = TrainingActivityEntity::class,
            parentColumn = "tId",
            entityColumn = "aId"
        )
    )
    val activities: List<ActivityEntity>
)