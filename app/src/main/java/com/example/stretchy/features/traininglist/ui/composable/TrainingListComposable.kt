package com.example.stretchy.features.traininglist.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.design.components.ActivityListScreen
import com.example.stretchy.design.components.DesignTheme
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

    DesignTheme {
        when (state) {
            is TrainingListUiState.Empty -> {
                // Show empty ActivityListScreen
                ActivityListScreen(
                    activities = emptyList(),
                    trainingType = trainingType,
                    onAdd = { navigationViewModel.navigateToCreateTraining(trainingType.name) },
                    onActivityClick = { /* No items to click */ },
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
                        // Find the original training by name and navigate to execute it
                        val training = state.trainings.find { it.name == activityItem.title }
                        training?.let { navigationViewModel.navigateToExecuteTraining(it.id) }
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
                    onActivityClick = { /* Error state */ },
                    onExportClick = onExportClick,
                    onImportClick = onImportClick,
                    onPerformExport = { viewModel.export() },
                    onPerformImport = { viewModel.import() }
                )
            }
        }
    }
}
