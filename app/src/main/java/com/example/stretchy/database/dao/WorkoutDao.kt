package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.WorkoutEntity

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(workouts: List<WorkoutEntity>): List<Long>

    @Update
    fun update(workout: WorkoutEntity)

    @Delete
    fun delete(workout: WorkoutEntity)

    @Query("SELECT * FROM workout WHERE workoutId = :id")
    fun getById(id: Long): WorkoutEntity?

    @Query("SELECT * FROM workout WHERE workoutId IN (:ids)")
    fun getByIds(ids: List<Long>): List<WorkoutEntity>

    @Query("SELECT * FROM workout")
    fun getAll(): List<WorkoutEntity>

    @Query("DELETE FROM workout WHERE workoutId = :id")
    fun deleteById(id: Long)
}

