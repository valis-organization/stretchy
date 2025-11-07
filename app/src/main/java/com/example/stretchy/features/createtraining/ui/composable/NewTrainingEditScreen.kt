package com.example.stretchy.features.createtraining.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.stretchy.design.components.TrainingEditScreen
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.ui.screen.ExerciseWidgetState

@Composable
fun NewTrainingEditScreen(
    navController: NavController,
    viewModel: CreateOrEditTrainingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExerciseNameEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var editingExerciseIndex by remember { mutableStateOf(-1) }
    var deletingExerciseIndex by remember { mutableStateOf(-1) }

    when (val state = uiState) {
        is CreateTrainingUiState.Success -> {
            // Convert ExercisesWithBreaks to ExerciseWidgetState
            val exerciseWidgetStates = state.exercisesWithBreaks.mapIndexed { index, exerciseWithBreak ->
                ExerciseWidgetState(
                    id = index + 1,
                    title = exerciseWithBreak.exercise.name.ifEmpty { "Exercise ${index + 1}" },
                    selectedTimeSeconds = exerciseWithBreak.exercise.duration,
                    customTimeSeconds = exerciseWithBreak.exercise.duration.toFloat(),
                    isTimelessExercise = exerciseWithBreak.exercise.duration <= 0,
                    breakTimeSeconds = exerciseWithBreak.nextBreakDuration ?: 0,
                    customBreakTimeSeconds = (exerciseWithBreak.nextBreakDuration ?: 0).toFloat(),
                    isBreakExpanded = false, // Break expansion is managed internally by the widget
                    isExpanded = exerciseWithBreak.isExpanded,
                    accentColor = generateExerciseColor(index)
                )
            }

            TrainingEditScreen(
                trainingName = state.currentName,
                exercises = exerciseWidgetStates,
                onBackClick = {
                    navController.popBackStack()
                },
                onTrainingNameEdit = { newName ->
                    viewModel.setTrainingName(newName)
                },
                onExerciseStateChange = { index, newState ->
                    // Convert back to ExercisesWithBreaks and update
                    val currentExercises = state.exercisesWithBreaks.toMutableList()
                    if (index < currentExercises.size) {
                        val updatedExercise = currentExercises[index].copy(
                            exercise = currentExercises[index].exercise.copy(
                                name = newState.title,
                                duration = if (newState.isTimelessExercise) 0 else newState.selectedTimeSeconds
                            ),
                            nextBreakDuration = if (newState.breakTimeSeconds > 0) newState.breakTimeSeconds else null,
                            isExpanded = newState.isExpanded // Only persist main exercise expansion state
                        )
                        currentExercises[index] = updatedExercise
                        viewModel.setExercises(currentExercises)
                    }
                },
                onAddExercise = {
                    // Add a new exercise
                    val currentExercises = state.exercisesWithBreaks.toMutableList()
                    val newExercise = ExercisesWithBreaks(
                        listId = currentExercises.size,
                        exercise = Exercise(
                            name = "New Exercise",
                            duration = 30
                        ),
                        nextBreakDuration = viewModel.getAutoBreakDuration(),
                        isExpanded = true
                    )
                    currentExercises.add(newExercise)
                    viewModel.setExercises(currentExercises)
                },
                onDeleteExercise = { index ->
                    // Show confirmation dialog before deleting
                    deletingExerciseIndex = index
                    showDeleteConfirmDialog = true
                },
                onEditExerciseName = { index ->
                    editingExerciseIndex = index
                    showExerciseNameEditDialog = true
                },
                onSave = {
                    if (state.editingTraining) {
                        viewModel.editTraining(viewModel.trainingId, state.exercisesWithBreaks)
                    } else {
                        viewModel.createTraining(state.exercisesWithBreaks)
                    }
                },
                canSave = state.saveButtonCanBeClicked,
                isEditing = state.editingTraining
            )


            // Exercise Name Edit Dialog
            if (showExerciseNameEditDialog && editingExerciseIndex >= 0 && editingExerciseIndex < state.exercisesWithBreaks.size) {
                ExerciseNameEditDialog(
                    currentName = state.exercisesWithBreaks[editingExerciseIndex].exercise.name,
                    onDismiss = {
                        showExerciseNameEditDialog = false
                        editingExerciseIndex = -1
                    },
                    onConfirm = { newName ->
                        val currentExercises = state.exercisesWithBreaks.toMutableList()
                        val updatedExercise = currentExercises[editingExerciseIndex].copy(
                            exercise = currentExercises[editingExerciseIndex].exercise.copy(name = newName)
                        )
                        currentExercises[editingExerciseIndex] = updatedExercise
                        viewModel.setExercises(currentExercises)
                        showExerciseNameEditDialog = false
                        editingExerciseIndex = -1
                    }
                )
            }

            // Delete Confirmation Dialog
            if (showDeleteConfirmDialog && deletingExerciseIndex >= 0 && deletingExerciseIndex < state.exercisesWithBreaks.size) {
                DeleteConfirmationDialog(
                    exerciseName = state.exercisesWithBreaks[deletingExerciseIndex].exercise.name.ifEmpty { "Exercise ${deletingExerciseIndex + 1}" },
                    onDismiss = {
                        showDeleteConfirmDialog = false
                        deletingExerciseIndex = -1
                    },
                    onConfirm = {
                        // Delete exercise at index
                        val currentExercises = state.exercisesWithBreaks.toMutableList()
                        if (deletingExerciseIndex < currentExercises.size && currentExercises.size > 1) {
                            currentExercises.removeAt(deletingExerciseIndex)
                            // Update listIds to maintain consistency
                            val updatedExercises = currentExercises.mapIndexed { newIndex, exercise ->
                                exercise.copy(listId = newIndex)
                            }
                            viewModel.setExercises(updatedExercises)
                        }
                        showDeleteConfirmDialog = false
                        deletingExerciseIndex = -1
                    }
                )
            }
        }
        is CreateTrainingUiState.Done -> {
            // Navigate back when done
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
        }
        is CreateTrainingUiState.Error -> {
            // Handle error states by showing the error and allowing the user to go back
            LaunchedEffect(state.reason) {
                // Handle specific error types if needed
                when (state.reason) {
                    is CreateTrainingUiState.Error.Reason.MissingTrainingName -> {
                        // Show error message for missing training name
                    }
                    is CreateTrainingUiState.Error.Reason.NotEnoughExercises -> {
                        // Show error message for not enough exercises
                    }
                    is CreateTrainingUiState.Error.Reason.Unknown -> {
                        // Show general error message
                    }
                }
            }
            // Show old screen as fallback for errors
            CreateTrainingScreen(navController, viewModel)
        }
        CreateTrainingUiState.Init -> {
            // Show loading indicator while initializing
            TrainingEditLoadingScreen()
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    exerciseName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Exercise") },
        text = {
            Text("Are you sure you want to delete \"$exerciseName\"? This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ExerciseNameEditDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(currentName)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Exercise Name") },
        text = {
            Column {
                Text("Enter a new name for this exercise:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Exercise Name") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(textFieldValue.text) },
                enabled = textFieldValue.text.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



// Generate colors for exercises
private fun generateExerciseColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFF66BB6A), // Light Green
        Color(0xFF81C784), // Lighter Green
        Color(0xFF2196F3), // Blue
        Color(0xFF64B5F6), // Light Blue
        Color(0xFF9C27B0), // Purple
        Color(0xFFBA68C8), // Light Purple
        Color(0xFFFF9800), // Orange
        Color(0xFFFFB74D), // Light Orange
        Color(0xFFF44336)  // Red
    )
    return colors[index % colors.size]
}

@Composable
private fun TrainingEditLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading training...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



