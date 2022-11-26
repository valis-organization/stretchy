package com.example.stretchy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
    version = 1
)
@TypeConverters(TrainingTypeConverter::class, ActivityTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun trainingDao(): TrainingDao
    abstract fun trainingWithActivitiesDao(): TrainingWithActivitiesDao

    companion object{
        const val NAME = "streachy_app"
    }
}