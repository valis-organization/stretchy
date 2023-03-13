package com.example.stretchy.features.datatransport

import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


class DataImporterImpl(val repository: Repository) : DataImporter {
    private val gson = Gson()

    override suspend fun importDataByAppending(path: String) {
        val file = File(path)
        val trainingsWithActivity = object : TypeToken<List<TrainingWithActivity>>() {}.type
        val data: String = file.inputStream().bufferedReader().use { it.readText() }
        val trainingsList =
            gson.fromJson<List<TrainingWithActivity>>(data, trainingsWithActivity)
        trainingsList.forEach { training ->
            repository.addTrainingWithActivities(training)
        }
    }
}