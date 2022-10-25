package com.example.stretchy.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.ExerciseInfo

val exercise1 = ExerciseInfo(
    itemName = "Recommendation name",
    numberOfExercises = 11,
    timeInSeconds = 397
)

val exercise2 = ExerciseInfo(
    itemName = "Exercise name",
    numberOfExercises = 7,
    timeInSeconds = 203
)

val exercisesList = mutableListOf(exercise1, exercise2)

@Composable
fun ExerciseListScreen(navController: NavController) {
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
            Column(modifier = Modifier.padding(top = 25.dp)) {
                Row(modifier = Modifier.padding(start = 25.dp)) {
                    Text(text = "Stretches", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                ExerciseList(exercises = exercisesList)
            }
        }
    }
}

@Composable
private fun ExerciseList(exercises: List<ExerciseInfo>) {
    LazyColumn {
        items(exercises) { exercise ->
            ExerciseListItem(item = exercise)
        }
    }
}

@Composable
private fun ExerciseListItem(item: ExerciseInfo) {
    Spacer(modifier = Modifier.height(25.dp))
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .height(150.dp)
            .padding(start = 25.dp, end = 25.dp, top = 10.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = item.itemName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(15.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(text = "Exercises", fontSize = 10.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${item.numberOfExercises}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(100.dp))
            Column {
                Text(text = "Total time", fontSize = 10.sp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = convertSecondsToMinutes(item.timeInSeconds),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun convertSecondsToMinutes(seconds: Int): String {
    val minutes = seconds / 60
    return "$minutes m ${seconds % 60}s"
}
