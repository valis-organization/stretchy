package com.example.stretchy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.stretchy.database.entity.ActivityEntity

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity")
    fun getAll(): List<ActivityEntity>

    @Query("SELECT * FROM activity WHERE name LIKE :activityName")
    fun findAllByName(activityName: String): List<ActivityEntity>

    @Insert
    fun add(activityEntity: ActivityEntity)

    @Delete
    fun delete(activityEntity: ActivityEntity)
}