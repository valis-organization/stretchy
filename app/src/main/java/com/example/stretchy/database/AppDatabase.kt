package com.example.stretchy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stretchy.database.converter.ActivityTypeConverter
import com.example.stretchy.database.converter.TrainingTypeConverter
import com.example.stretchy.database.dao.ActivityDao
import com.example.stretchy.database.dao.TrainingDao
import com.example.stretchy.database.dao.TrainingWithActivitiesDao
import com.example.stretchy.database.entity.ActivityEntity
import com.example.stretchy.database.entity.TrainingActivityEntity
import com.example.stretchy.database.entity.TrainingEntity

@Database(
    entities = [TrainingEntity::class, ActivityEntity::class, TrainingActivityEntity::class],
    version = 2
)
@TypeConverters(TrainingTypeConverter::class, ActivityTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun trainingDao(): TrainingDao
    abstract fun trainingWithActivitiesDao(): TrainingWithActivitiesDao

    companion object {
        const val NAME = "stretchydb"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("ALTER TABLE training_activities ADD COLUMN activityOrder INTEGER NOT NULL DEFAULT 0")

                database.execSQL(
                    "UPDATE training_activities " +
                            "SET activityOrder = (SELECT COUNT(*) " +
                            "                    FROM training_activities AS ta " +
                            "                    WHERE ta.tId = training_activities.tId " +
                            "                    AND ta.aId <= training_activities.aId)"
                )
            }
        }
    }
}