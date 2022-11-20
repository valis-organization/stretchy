package com.example.stretchy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.stretchy.database.entity.TrainingEntity

@Dao
interface TrainingDao {
    @Query("SELECT * FROM training")
    fun getAll(): List<TrainingEntity>

    @Insert
    fun add(trainingEntity: TrainingEntity)

    @Delete
    fun delete(trainingEntity: TrainingEntity)
}