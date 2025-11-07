package com.example.stretchy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.stretchy.database.entity.TrainingSequenceEntity
import com.example.stretchy.database.data.ActivityType

@Dao
interface TrainingSequenceDao {

    @Query("""
        SELECT ts.trainingId, ts.sequenceOrder, ts.exerciseId, ts.followingBreakId,
               a.name, a.duration, a.activityType,
               COALESCE(bt.duration, 0) as breakDuration
        FROM training_sequence ts
        JOIN activity a ON ts.exerciseId = a.activityId
        LEFT JOIN break_templates bt ON ts.followingBreakId = bt.breakId
        WHERE ts.trainingId = :trainingId
        ORDER BY ts.sequenceOrder ASC
    """)
    suspend fun getTrainingSequenceWithDetails(trainingId: Long): List<TrainingSequenceWithDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSequenceItem(sequenceItem: TrainingSequenceEntity)

    @Query("DELETE FROM training_sequence WHERE trainingId = :trainingId")
    suspend fun deleteTrainingSequence(trainingId: Long)

    @Transaction
    suspend fun replaceTrainingSequence(trainingId: Long, sequence: List<TrainingSequenceEntity>) {
        deleteTrainingSequence(trainingId)
        sequence.forEach { insertSequenceItem(it) }
    }
}

/**
 * Combined data class for training sequence with exercise and break details
 */
data class TrainingSequenceWithDetails(
    val trainingId: Long,
    val sequenceOrder: Int,
    val exerciseId: Long,
    val followingBreakId: String?,
    // Exercise details
    val name: String,
    val duration: Int,
    val activityType: ActivityType,
    // Break details (nullable)
    val breakDuration: Int
)
