package com.example.stretchy.features.traininglist.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.common.convertSecondsToMinutes
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState

@Composable
fun TrainingsComposable(
    navController: NavController,
    exercisePlansViewModel: TrainingListViewModel = viewModel()
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.ExerciseCreatorScreen.route) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .background(Color.LightGray)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(modifier = Modifier.padding(start = 24.dp)) {
                    Text(
                        text = stringResource(R.string.stretches),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                when (val state = exercisePlansViewModel.uiState.collectAsState().value) {
                    is TrainingListUiState.Empty ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.exercises_not_added),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.do_it_by_add_button),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    is TrainingListUiState.Loading ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    is TrainingListUiState.Loaded -> TrainingsComposable(
                        state.trainings,
                        navController
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingsComposable(trainingList: List<Training>, navController: NavController) {
    LazyColumn {
        items(trainingList) { exercise ->
            Box(modifier = Modifier.clickable { navController.navigate(Screen.ExerciseScreen.route) }) {
                TrainingComposable(item = exercise)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrainingComposable(item: Training) {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .height(152.dp)
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = item.itemName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = stringResource(R.string.exercises),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${item.numberOfExercises}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(100.dp))
            Column {
                Text(
                    text = stringResource(R.string.total_time),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = convertSecondsToMinutes(item.timeInSeconds.toLong()),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

