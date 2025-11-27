package com.example.stretchy.database.dao

import androidx.room.*
import com.example.stretchy.database.entity.ActivityEntity

/**
 * @deprecated Old structure from version 1-4. Kept only for migration code compatibility.
 * After MIGRATION_4_5, the 'activity' table no longer exists in the database.
 * Use WorkoutDao instead for the new structure (version 5+).
 */
@Deprecated(
    message = "Old structure - use WorkoutDao instead",
    replaceWith = ReplaceWith("WorkoutDao"),
    level = DeprecationLevel.WARNING
)
@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity")
    fun getAll(): List<ActivityEntity>

    @Query("SELECT * FROM activity WHERE name LIKE :activityName")
    fun findAllByName(activityName: String): List<ActivityEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(activityEntity: ActivityEntity): Long

    @Query("SELECT * FROM activity WHERE name = :name AND duration = :duration")
    fun getConflictActivity(name: String, duration: Int): ActivityEntity?

    @Delete
    fun delete(activityEntity: ActivityEntity)

    @Update
    fun update(activityEntity: ActivityEntity)
}