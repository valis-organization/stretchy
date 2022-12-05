package com.example.stretchy.features.datatransport

import com.example.stretchy.repository.Repository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class DataExporterExporterImpl(val repository: Repository) : DataExporter {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val dataFile = File(dataTransportFilePath, dataTransportFileName)

    override suspend fun saveDataInFile() {
        val data = getSavedData()
        dataFile.writeText(data!!)
    }

    private suspend fun getSavedData(): String? {
        var data: String? = null
        CoroutineScope(Dispatchers.Main).launch {
            data = gson.toJson(repository.getTrainingsWithActivities())
        }.join()
        return data
    }

}