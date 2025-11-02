package com.example.stretchy

sealed class Screen(val route: String) {
    companion object {
        const val ARG_ID = "id"
        const val ARG_TRAINING_TYPE = "trainingType"
    }

    object StretchingListScreen : Screen("stretchingListScreen")
    object ExerciseCreatorScreen : Screen("exerciseCreatorScreen?id={id}&trainingType={trainingType}") {
        fun createRoute(id: String = "-1", trainingType: String = "BODYWEIGHT"): String {
            return "exerciseCreatorScreen?id=$id&trainingType=$trainingType"
        }
    }
    object ExecuteTrainingScreen : Screen("executeTraining?id={id}") {
        fun createRoute(id: String): String = "executeTraining?id=$id"
    }
    object TrainingListScreen : Screen("trainingListScreen")
    object MetaTrainingScreen : Screen("metaTrainingScreen")
}
