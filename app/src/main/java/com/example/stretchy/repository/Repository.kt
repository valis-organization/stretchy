package com.example.stretchy.repository

import com.example.stretchy.features.traininglist.ui.data.Training

interface Repository {
    suspend fun getTrainings(): List<Training>

    suspend fun getActivitiesForTraining(id: String): List<ActivityDomain>
}