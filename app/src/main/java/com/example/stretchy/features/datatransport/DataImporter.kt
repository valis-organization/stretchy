package com.example.stretchy.features.datatransport

interface DataImporter {
    suspend fun importDataByAppending(data: String)

    suspend fun importDataByOverriding(data:String)
}