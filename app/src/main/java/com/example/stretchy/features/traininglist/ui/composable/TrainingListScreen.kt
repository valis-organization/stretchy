package com.example.stretchy.features.traininglist.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.design.components.ActivityListScreen
import com.example.stretchy.design.components.toActivityItem
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.navigation.NavigationViewModel
import com.example.stretchy.Screen

@Composable
fun TrainingListScreen(
    viewModel: TrainingListViewModel,
    navigationViewModel: NavigationViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    trainingType: TrainingType
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is TrainingListUiState.Loaded -> {
            ActivityListScreen(
                activities = state.trainings.map { it.toActivityItem() },
                trainingType = trainingType,
                onAdd = {
                    navigationViewModel.navigateToCreateTraining(trainingType = trainingType.toString())
                },
                onActivityClick = { activityItem ->
                    navigationViewModel.navigateToExecuteTraining(activityItem.id)
                },
                onActivityEdit = { activityItem ->
                    navigationViewModel.navigateToEditTraining(trainingId = activityItem.id, trainingType = trainingType.toString())
                },
                onActivityDelete = { activityItem ->
                    // Find the Training object from the state
                    val trainingToDelete = state.trainings.find { it.id == activityItem.id }
                    trainingToDelete?.let { viewModel.deleteTraining(it) }
                },
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                onPerformExport = { viewModel.export() },
                onPerformImport = { viewModel.import() }
            )
        }
        is TrainingListUiState.Loading -> {
             Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is TrainingListUiState.Empty -> {
             ActivityListScreen(
                activities = emptyList(),
                trainingType = trainingType,
                onAdd = {
                    navigationViewModel.navigateToCreateTraining(trainingType = trainingType.toString())
                },
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                onPerformExport = { viewModel.export() },
                onPerformImport = { viewModel.import() }
            )
        }
        is TrainingListUiState.Error -> {
             ActivityListScreen(
                activities = emptyList(),
                trainingType = trainingType,
                onAdd = {
                    navigationViewModel.navigateToCreateTraining(trainingType = trainingType.toString())
                },
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                onPerformExport = { viewModel.export() },
                onPerformImport = { viewModel.import() }
            )
        }
    }
}
