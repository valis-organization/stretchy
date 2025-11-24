package com.example.stretchy.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stretchy.database.entity.BreakEntity

private const val MIG_TAG = "MIG_3"

@Dao
interface BreakDao {

    @Query("SELECT * FROM breaks WHERE duration = :duration LIMIT 1")
    fun findByDuration(duration: Int): BreakEntity?

    @Query("SELECT COUNT(*) FROM training_activities WHERE breakId = :breakId")
    fun getUsageCount(breakId: Long): Int

    @Query("SELECT * FROM breaks WHERE breakId = :breakId")
    fun getById(breakId: Long): BreakEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(breakEntity: BreakEntity): Long

    @Update
    fun update(breakEntity: BreakEntity): Int

    @Delete
    fun delete(breakEntity: BreakEntity): Int

    @Query("DELETE FROM breaks WHERE breakId = :breakId")
    fun deleteById(breakId: Long): Int

    @Query("""
        DELETE FROM breaks 
        WHERE breakId NOT IN (
            SELECT DISTINCT breakId 
            FROM training_activities 
            WHERE breakId IS NOT NULL
        )
    """)
    fun deleteUnused(): Int

    @Query("SELECT * FROM breaks")
    fun getAll(): List<BreakEntity>

}

// Extension function to handle break creation with comprehensive logging
fun BreakDao.findOrCreateBreak(duration: Int): BreakEntity {
    Log.d(MIG_TAG, "Finding or creating break with duration: $duration")

    val existing = findByDuration(duration)
    if (existing != null) {
        Log.d(MIG_TAG, "Found existing break: id=${existing.breakId}, duration=$duration")
        return existing
    }

    val newBreak = BreakEntity(duration = duration)
    val newId = insert(newBreak)
    if (newId > 0) {
        val result = newBreak.copy(breakId = newId)
        Log.d(MIG_TAG, "Created new break: id=$newId, duration=$duration")
        return result
    } else {
        // If insert failed (likely due to IGNORE conflict), try to find again
        val retryFind = findByDuration(duration)
        if (retryFind != null) {
            Log.d(MIG_TAG, "Found break after insert conflict: id=${retryFind.breakId}, duration=$duration")
            return retryFind
        } else {
            throw IllegalStateException("Failed to create or find break with duration: $duration")
        }
    }
}


