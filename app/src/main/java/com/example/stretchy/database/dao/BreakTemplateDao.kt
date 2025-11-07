package com.example.stretchy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stretchy.database.entity.BreakTemplateEntity

@Dao
interface BreakTemplateDao {

    @Query("SELECT * FROM break_templates ORDER BY usageCount DESC, duration ASC")
    suspend fun getAllBreakTemplates(): List<BreakTemplateEntity>

    @Query("SELECT * FROM break_templates WHERE duration = :duration LIMIT 1")
    suspend fun findBreakByDuration(duration: Int): BreakTemplateEntity?

    @Query("SELECT * FROM break_templates WHERE breakId = :breakId")
    suspend fun getBreakTemplate(breakId: String): BreakTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakTemplate(breakTemplate: BreakTemplateEntity)

    @Update
    suspend fun updateBreakTemplate(breakTemplate: BreakTemplateEntity)

    @Query("UPDATE break_templates SET usageCount = usageCount + :increment WHERE breakId = :breakId")
    suspend fun updateUsageCount(breakId: String, increment: Int)

    @Query("DELETE FROM break_templates WHERE breakId = :breakId AND usageCount = 0")
    suspend fun deleteUnusedBreak(breakId: String)
}
