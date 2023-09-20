package com.example.stretchy.features.createtraining.ui.composable.buttons

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity

@Composable
fun AddOrEditExerciseButton(
    exerciseName: String,
    exerciseDuration: Int,
    viewModel: CreateOrEditTrainingViewModel,
    editedExercise: Exercise,
    onAddOrEditButtonClick: () -> Unit
) {
    val context = LocalContext.current
    val exerciseIsBeingEdited: Boolean = editedExercise.name != ""
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 2.dp),
        onClick = {
            if (exerciseName.isNotEmpty() && exerciseDuration != 0) {
                onAddOrEditButtonClick()
                if (exerciseIsBeingEdited) {
                    if (exerciseName != editedExercise.name || exerciseDuration != editedExercise.duration) {
                        viewModel.editActivity(
                            Activity(
                                exerciseName,
                                editedExercise.activityOrder,
                                exerciseDuration,
                                ActivityType.STRETCH
                            )
                        )
                        Toast.makeText(context, R.string.exercise_edited, Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    viewModel.addActivity(
                        Activity(
                            exerciseName,
                            null,
                            exerciseDuration,
                            ActivityType.STRETCH
                        )
                    )
                    Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(
                    context,
                    R.string.specify_exercise_name,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    ) {
        if (exerciseIsBeingEdited) {
            if (exerciseName == editedExercise.name && exerciseDuration == editedExercise.duration) {
                Text(text = stringResource(id = R.string.close_item))
            } else {
                Text(text = stringResource(id = R.string.save_changes))
            }
        } else {
            Text(text = stringResource(id = R.string.add_exercise))
        }
    }
}