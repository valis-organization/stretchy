package com.example.stretchy.database.converter

import androidx.room.TypeConverter
import com.example.stretchy.database.data.WorkoutType

class WorkoutTypeConverter {
    @TypeConverter
    fun fromWorkoutType(value: WorkoutType): String {
        return value.name
    }

    @TypeConverter
    fun toWorkoutType(value: String): WorkoutType {
        return WorkoutType.valueOf(value)
    }
}

