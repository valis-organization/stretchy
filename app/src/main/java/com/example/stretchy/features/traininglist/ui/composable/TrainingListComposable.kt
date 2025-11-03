package com.example.stretchy.features.traininglist.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.features.traininglist.ui.composable.list.TrainingLazyListVieww
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.theme.White80

@Composable
fun TrainingListScreenn(
    viewModel: TrainingListViewModel,
    navController: NavController,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    trainingType: TrainingType
) {
    val state = viewModel.uiState.collectAsState().value
    TrainingListVieww(
        state = state,
        navController = navController,
        trainingType = trainingType,
        onExportClick = onExportClick,
        onImportClick = onImportClick,
        onPerformExport = { viewModel.export() },
        onPerformImport = { viewModel.import() },
        onDeleteTraining = { training -> viewModel.deleteTraining(training) },
        onCopyTraining = { training -> viewModel.copyTraining(training) }
    )
}

@Composable
private fun TrainingListVieww(
    state: TrainingListUiState,
    navController: NavController,
    trainingType: TrainingType,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onPerformExport: () -> Unit,
    onPerformImport: suspend () -> Unit,
    onDeleteTraining: (Training) -> Unit,
    onCopyTraining: (Training) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.ExerciseCreatorScreen.createRoute(trainingType = trainingType.name))
            }) { Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.desc_plus_icon)) }
        },
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }, actions = {
                Menu(
                    onRequestExportPermission = onExportClick,
                    onRequestImportPermission = onImportClick,
                    onPerformExport = onPerformExport,
                    onPerformImport = onPerformImport
                )
            })
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .background(White80)
                .fillMaxSize()
        ) {
            Column(Modifier.padding(top = 24.dp)) {
                when (state) {
                    is TrainingListUiState.Empty -> Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.exercises_not_added), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(stringResource(R.string.do_it_by_add_button), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    is TrainingListUiState.Loading -> Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { CircularProgressIndicator() }
                    is TrainingListUiState.Loaded -> TrainingLazyListVieww(
                        trainingList = state.trainings,
                        navController = navController,
                        onDeleteTraining = onDeleteTraining,
                        onCopyTraining = onCopyTraining
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Training List - Full Integration", showBackground = true)
@Composable
private fun TrainingListFullIntegrationPreview() {
    val sampleTrainings = listOf(
        Training(
            id = "1",
            name = "Morning Routine",
            numberOfExercises = 8,
            timeInSeconds = 600,
            type = Training.Type.STRETCH
        ),
        Training(
            id = "2",
            name = "HIIT Workout",
            numberOfExercises = 12,
            timeInSeconds = 900,
            type = Training.Type.BODY_WEIGHT
        ),
        Training(
            id = "3",
            name = "Evening Cool Down",
            numberOfExercises = 5,
            timeInSeconds = 300,
            type = Training.Type.STRETCH
        )
    )

    TrainingListVieww(
        state = TrainingListUiState.Loaded(sampleTrainings),
        navController = androidx.navigation.compose.rememberNavController(),
        trainingType = TrainingType.STRETCH,
        onExportClick = {},
        onImportClick = {},
        onPerformExport = {},
        onPerformImport = {},
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

@androidx.compose.ui.tooling.preview.Preview(name = "Training List - Empty", showBackground = true)
@Composable
private fun TrainingListEmptyPreview() {
    TrainingListVieww(
        state = TrainingListUiState.Empty,
        navController = androidx.navigation.compose.rememberNavController(),
        trainingType = TrainingType.STRETCH,
        onExportClick = {},
        onImportClick = {},
        onPerformExport = {},
        onPerformImport = {},
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

@androidx.compose.ui.tooling.preview.Preview(name = "Training List - Loaded", showBackground = true)
@Composable
private fun TrainingListLoadedPreview() {
    val sampleTrainings = listOf(
        Training(
            id = "1",
            name = "Morning Routine",
            numberOfExercises = 6,
            timeInSeconds = 420,
            type = Training.Type.STRETCH
        ),
        Training(
            id = "2",
            name = "Evening Routine",
            numberOfExercises = 4,
            timeInSeconds = 240,
            type = Training.Type.STRETCH
        )
    )

    TrainingListVieww(
        state = TrainingListUiState.Loaded(sampleTrainings),
        navController = androidx.navigation.compose.rememberNavController(),
        trainingType = TrainingType.STRETCH,
        onExportClick = {},
        onImportClick = {},
        onPerformExport = {},
        onPerformImport = {},
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}

@androidx.compose.ui.tooling.preview.Preview(name = "Training List - Loading", showBackground = true)
@Composable
private fun TrainingListLoadingPreview() {
    TrainingListVieww(
        state = TrainingListUiState.Loading,
        navController = androidx.navigation.compose.rememberNavController(),
        trainingType = TrainingType.STRETCH,
        onExportClick = {},
        onImportClick = {},
        onPerformExport = {},
        onPerformImport = {},
        onDeleteTraining = {},
        onCopyTraining = {}
    )
}
