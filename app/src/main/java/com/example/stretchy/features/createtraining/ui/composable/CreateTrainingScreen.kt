package com.example.stretchy.features.createtraining.ui.composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel

@Composable
fun CreateTrainingScreen(
    navController: NavController,
    viewModel: CreateOrEditTrainingViewModel
) {
    // Fallback for CreateTrainingScreen if it was missing
    NewTrainingEditScreen(navController, viewModel)
}
