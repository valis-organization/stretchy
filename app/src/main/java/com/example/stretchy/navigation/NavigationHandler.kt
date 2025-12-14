package com.example.stretchy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun HandleNavigationEvents(
    navEvents: SharedFlow<NavigationViewModel.NavEvent>,
    navController: NavController
) {
    LaunchedEffect(navEvents) {
        navEvents.collect { event ->
            when (event) {
                is NavigationViewModel.NavEvent.CreateTraining -> {
                    navController.navigate(
                        com.example.stretchy.Screen.ExerciseCreatorScreen.createRoute(
                            trainingType = event.trainingType
                        )
                    )
                }
                is NavigationViewModel.NavEvent.EditTraining -> {
                    navController.navigate(
                        com.example.stretchy.Screen.ExerciseCreatorScreen.createRoute(
                            id = event.trainingId,
                            trainingType = event.trainingType
                        )
                    )
                }
                is NavigationViewModel.NavEvent.ExecuteTraining -> {
                    navController.navigate(
                        com.example.stretchy.Screen.ExecuteTrainingScreen.createRoute(event.trainingId)
                    )
                }
                is NavigationViewModel.NavEvent.Back -> {
                    navController.popBackStack()
                }
                is NavigationViewModel.NavEvent.NavToRoute -> {
                    navController.navigate(event.route)
                }
            }
        }
    }
}
