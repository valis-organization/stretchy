package com.example.stretchy.database.converter

import androidx.room.TypeConverter
import com.example.stretchy.database.data.ActivityType

class ActivityTypeConverter {

    @TypeConverter
    fun fromActivityType(activityType: ActivityType): String {
        return activityType.name
    }

    @TypeConverter
    fun toActivityType(activityType: String): ActivityType {
        return ActivityType.valueOf(activityType)
    }
}