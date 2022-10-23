package com.example.stretchy.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stretchy.navigation.screens.MainScreenView
import com.example.stretchy.navigation.screens.PlanningScreenView
import com.example.stretchy.navigation.screens.Screen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreenView(navController = navController)
        }
        composable(route = Screen.PlanningScreen.route) {
            PlanningScreenView()
        }
    }
}