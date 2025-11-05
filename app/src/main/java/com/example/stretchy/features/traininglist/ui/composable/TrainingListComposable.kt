package com.example.stretchy.features.traininglist.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
                // Show loading state with ActivityListScreen
                ActivityListScreen(
                    activities = emptyList(),
                    trainingType = trainingType,
                    onAdd = { navigationViewModel.navigateToCreateTraining(trainingType.name) },
                    onActivityClick = { /* Loading state */ },
                    onActivityEdit = { /* Loading state */ },
                    onActivityCopy = { /* Loading state */ },
                    onActivityDelete = { /* Loading state */ },
                    onExportClick = onExportClick,
                    onImportClick = onImportClick,
                    onPerformExport = { viewModel.export() },
                    onPerformImport = { viewModel.import() }
                )
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
                // Show error state with ActivityListScreen
                ActivityListScreen(
                    activities = emptyList(),
                    trainingType = trainingType,
                    onAdd = { navigationViewModel.navigateToCreateTraining(trainingType.name) },
                    onActivityClick = { /* Empty state */ },
                    onActivityEdit = { /* Empty state */ },
                    onActivityCopy = { /* Empty state */ },
                    onActivityDelete = { /* Empty state */ },
                    onExportClick = onExportClick,
                    onImportClick = onImportClick,
                    onPerformExport = { viewModel.export() },
                    onPerformImport = { viewModel.import() }
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
