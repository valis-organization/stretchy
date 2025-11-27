package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingWithActivitiesEntity

/**
 * @deprecated Old structure from version 1-4. Kept only for migration code compatibility.
 * After MIGRATION_4_5, the 'training_activities' table no longer exists in the database.
 * Training sequences are now stored as comma-separated workout IDs in TrainingEntity.sequence.
 * Use TrainingDao and WorkoutDao instead.
 */
@Deprecated(
    message = "Old structure - use TrainingDao.sequence with WorkoutDao instead",
    replaceWith = ReplaceWith("TrainingDao"),
    level = DeprecationLevel.WARNING
)
@Dao
interface TrainingWithActivitiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trainingActivity: TrainingActivityEntity)

    @Transaction
    @Query("SELECT * FROM training")
    fun getTrainings(): List<TrainingWithActivitiesEntity>

    @Transaction
    @Query("SELECT * FROM training WHERE trainingId LIKE :id ")
    fun getTrainingsById(id: Long): TrainingWithActivitiesEntity

    @Transaction
    @Query("SELECT * FROM training_activities WHERE tId LIKE :trainingId ")
    fun getTrainingsActivitiesByTrainingId(trainingId: Long): List<TrainingActivityEntity>

    @Delete
    fun delete(trainingActivity: TrainingActivityEntity)

    @Update
    fun update(trainingActivityEntity: TrainingActivityEntity)

    @Query("UPDATE training_activities SET breakId = :breakId WHERE tId = :trainingId AND activityOrder = :activityOrder")
    fun updateBreakId(trainingId: Long, activityOrder: Int, breakId: Long)
}