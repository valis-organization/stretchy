package com.example.stretchy.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * @deprecated Old structure from version 1-4. Related tables dropped in MIGRATION_4_5.
 * Use TrainingEntity with sequence field (version 5+).
 */
@Deprecated(
    message = "Old structure - use TrainingEntity with sequence field instead",
    replaceWith = ReplaceWith("TrainingEntity"),
    level = DeprecationLevel.WARNING
)
data class TrainingWithActivitiesEntity(
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