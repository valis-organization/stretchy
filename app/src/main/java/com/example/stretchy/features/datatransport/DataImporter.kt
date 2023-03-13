package com.example.stretchy.features.datatransport

interface DataImporter {
    suspend fun importDataByAppending(path: String)
}