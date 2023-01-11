package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.TrainingEntity

@Dao
interface TrainingDao {
    @Query("SELECT * FROM training")
    fun getAll(): List<TrainingEntity>

    @Insert
    fun add(trainingEntity: TrainingEntity)

    @Delete
    fun delete(trainingEntity: TrainingEntity)

    @Update
    fun update(trainingEntity: TrainingEntity)
}