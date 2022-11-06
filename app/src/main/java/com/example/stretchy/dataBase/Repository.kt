package com.example.stretchy.dataBase

import com.example.stretchy.ui.theme.ExercisePlanItem

class Repository(private val db: StretchyDataBase) {

    suspend fun getPlansList(): List<ExercisePlanItem> {
        return db.getPlansList()
    }

    suspend fun getActivities(): List<ActivityRepo> {
        val exerciseDb = db.getExercisesList()
        val activities = mutableListOf<ActivityRepo>()
        exerciseDb.forEachIndexed { index, it ->
            activities.add(ActivityRepo.ExerciseRepo(it.name, it.duration))
            if (index != exerciseDb.lastIndex) {
                activities.add(ActivityRepo.BreakRepo(5))
            }
        }
        return activities
    }
}