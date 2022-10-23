package com.example.stretchy.navigation.screens

sealed class Screen(val route: String) {
    object MainScreen : Screen("mainScreen")
    object PlanningScreen : Screen("planningScreen")
}
