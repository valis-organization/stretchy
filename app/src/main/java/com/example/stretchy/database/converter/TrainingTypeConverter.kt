package com.example.stretchy.database.converter

import androidx.room.TypeConverter
import com.example.stretchy.database.data.TrainingType

class TrainingTypeConverter {

    @TypeConverter
    fun fromTrainingType(trainingType: TrainingType): String {
        return trainingType.name
    }

    @TypeConverter
    fun toTrainingType(trainingType: String): TrainingType {
        return TrainingType.valueOf(trainingType)
    }
}