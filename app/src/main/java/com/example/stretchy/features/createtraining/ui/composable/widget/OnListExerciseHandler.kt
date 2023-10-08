package com.example.stretchy.features.createtraining.ui.composable.widget

import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks

interface OnListExerciseHandler {

    fun addExercise(exercise : ExercisesWithBreaks)

    fun editExercise(exercise: ExercisesWithBreaks)

    fun removeBreak(position : Int)
}