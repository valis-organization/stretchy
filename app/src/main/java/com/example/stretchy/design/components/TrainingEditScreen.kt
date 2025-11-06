package com.example.stretchy.design.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.ui.screen.ExerciseWidget
import com.example.stretchy.ui.screen.ExerciseWidgetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingEditScreen(
    modifier: Modifier = Modifier,
    trainingName: String = "Upper Body Release",
    exercises: List<ExerciseWidgetState> = emptyList(),
    onBackClick: () -> Unit = {},
    onTrainingNameEdit: () -> Unit = {},
    onExerciseStateChange: (Int, ExerciseWidgetState) -> Unit = { _, _ -> },
    onAddExercise: () -> Unit = {}
) {
    val exerciseCount = exercises.size
    val breakCount = exercises.count { it.breakTimeSeconds > 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Editing Training",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExercise,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Exercise",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Training Name Section
            item {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Training Name",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = trainingName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = onTrainingNameEdit,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Training Name",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Exercise Summary
            item {
                Text(
                    text = "$exerciseCount exercises • $breakCount breaks",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Exercise List
            itemsIndexed(exercises) { index, exercise ->
                ExerciseWidget(
                    state = exercise,
                    onStateChange = { newState ->
                        onExerciseStateChange(index, newState)
                    },
                    numberCircleSize = 18.5.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bottom padding for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Training Edit Screen")
@Composable
fun TrainingEditScreenPreview() {
    StretchingTheme {
        val sampleExercises = listOf(
            ExerciseWidgetState(
                id = 1,
                title = "Neck Rolls",
                selectedTimeSeconds = 30,
                customTimeSeconds = 30f,
                isTimelessExercise = false,
                breakTimeSeconds = 10,
                customBreakTimeSeconds = 10f,
                isBreakExpanded = false,
                isExpanded = false,
                accentColor = Color(0xFF4CAF50)
            ),
            ExerciseWidgetState(
                id = 2,
                title = "Shoulder Stretch",
                selectedTimeSeconds = 45,
                customTimeSeconds = 45f,
                isTimelessExercise = false,
                breakTimeSeconds = 15,
                customBreakTimeSeconds = 15f,
                isBreakExpanded = false,
                isExpanded = false,
                accentColor = Color(0xFF66BB6A)
            ),
            ExerciseWidgetState(
                id = 3,
                title = "Side Bend",
                selectedTimeSeconds = 60,
                customTimeSeconds = 60f,
                isTimelessExercise = true,
                breakTimeSeconds = 0,
                customBreakTimeSeconds = 0f,
                isBreakExpanded = false,
                isExpanded = false,
                accentColor = Color(0xFF81C784)
            ),
            ExerciseWidgetState(
                id = 4,
                title = "Hamstring Stretch",
                selectedTimeSeconds = 60,
                customTimeSeconds = 60f,
                isTimelessExercise = false,
                breakTimeSeconds = 10,
                customBreakTimeSeconds = 10f,
                isBreakExpanded = false,
                isExpanded = false,
                accentColor = Color(0xFF4CAF50)
            )
        )

        TrainingEditScreen(
            trainingName = "Upper Body Release",
            exercises = sampleExercises
        )
    }
}
