package com.example.stretchy.features.traininglist.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.design.components.ActivityListScreen
import com.example.stretchy.design.components.StretchingTheme
import com.example.stretchy.design.components.TrainingTheme
import com.example.stretchy.design.components.toActivityItem
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.navigation.NavigationViewModel
import kotlin.random.Random

@Composable
fun TrainingListScreenn(
    viewModel: TrainingListViewModel,
    navigationViewModel: NavigationViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    trainingType: TrainingType
) {
    val state = viewModel.uiState.collectAsState().value

    // Use appropriate theme based on training type
    val content: @Composable () -> Unit = {
        when (state) {
            is TrainingListUiState.Empty -> {
                // Show empty ActivityListScreen
                ActivityListScreen(
                    activities = emptyList(),
                    trainingType = trainingType,
                    onAdd = { navigationViewModel.navigateToCreateTraining(trainingType.name) },
                    onActivityClick = { /* Error state */ },
                    onActivityEdit = { /* Error state */ },
                    onActivityCopy = { /* Error state */ },
                    onActivityDelete = { /* Error state */ },
                    onExportClick = onExportClick,
                    onImportClick = onImportClick,
                    onPerformExport = { viewModel.export() },
                    onPerformImport = { viewModel.import() }
                )
            }
            is TrainingListUiState.Loading -> {
                // Show Material 3 loading indicator
                LoadingScreen()
            }
            is TrainingListUiState.Loaded -> {
                // Convert trainings to activities and show them
                val activities = state.trainings.map { training ->
                    // Generate some sample data for streak and last exercised
                    // In real implementation, this would come from a database or user tracking
                    val streakCount = Random.nextInt(1, 10)
                    val lastExercised = when (Random.nextInt(5)) {
                        0 -> "today"
                        1 -> "1d ago"
                        2 -> "2d ago"
                        3 -> "1w ago"
                        else -> "2w ago"
                    }
                    training.toActivityItem(streakCount = streakCount, lastExercised = lastExercised)
                }

                ActivityListScreen(
                    activities = activities,
                    trainingType = trainingType,
                    onAdd = { navigationViewModel.navigateToCreateTraining(trainingType.name) },
                    onActivityClick = { activityItem ->
                        // Navigate to execute training using the activity's ID
                        navigationViewModel.navigateToExecuteTraining(activityItem.id)
                    },
                    onActivityEdit = { activityItem ->
                        // Navigate to edit training using the activity's ID
                        navigationViewModel.navigateToEditTraining(activityItem.id, trainingType.name)
                    },
                    onActivityCopy = { activityItem ->
                        // TODO: Implement copy functionality
                    },
                    onActivityDelete = { activityItem ->
                        // TODO: Implement delete functionality
                        // viewModel.deleteTraining(activityItem.id)
                    },
                    onExportClick = onExportClick,
                    onImportClick = onImportClick,
                    onPerformExport = { viewModel.export() },
                    onPerformImport = { viewModel.import() }
                )
            }
            is TrainingListUiState.Error -> {
                // Show error screen with retry option
                ErrorScreen(
                    onRetry = { viewModel.loadTrainings() },
                    onAdd = { navigationViewModel.navigateToCreateTraining(trainingType.name) }
                )
            }
        }
    }

    // Apply appropriate theme based on training type
    when (trainingType) {
        TrainingType.STRETCH -> StretchingTheme { content() }
        TrainingType.BODYWEIGHT -> TrainingTheme { content() }
    }
}

@Composable
private fun LoadingScreen() {
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
                text = "Loading trainings...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    onRetry: () -> Unit,
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Failed to load trainings",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please check your connection and try again",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.size(width = 120.dp, height = 40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }

        // Floating Action Button for adding new training
        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Training"
            )
        }
    }
}

