package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.TrainingEntity

@Dao
interface TrainingDao {
    @Query("SELECT * FROM training")
    fun getAll(): List<TrainingEntity>

    @Query("SELECT * FROM training WHERE trainingId = :trainingId")
    fun getById(trainingId: Long): TrainingEntity?

    @Insert
    fun add(trainingEntity: TrainingEntity)

    @Delete
    fun delete(trainingEntity: TrainingEntity)

    @Query("DELETE FROM training WHERE trainingId = :trainingId")
    fun deleteById(trainingId: Long)

    @Update
    fun update(trainingEntity: TrainingEntity)
}