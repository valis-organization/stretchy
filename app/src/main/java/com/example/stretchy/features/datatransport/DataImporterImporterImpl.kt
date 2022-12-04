package com.example.stretchy.features.datatransport

import com.example.stretchy.repository.Repository
import com.example.stretchy.repository.TrainingWithActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class DataImporterImporterImpl(val repository: Repository) : DataImporter {
    private val gson = Gson()
    private val dataFile = File(dataTransportFilePath, dataTransportFileName)

    override fun addSavedDataToDb() {
        val trainingsWithActivity = object : TypeToken<List<TrainingWithActivity>>() {}.type
        val data: String = dataFile.inputStream().bufferedReader().use { it.readText() }
        val trainingsList =  gson.fromJson<List<TrainingWithActivity>>(data, trainingsWithActivity)
        CoroutineScope(Dispatchers.Main).launch {
            trainingsList.forEach{ training ->
                repository.addTrainingWithActivities(training)
            }
        }
    }
}