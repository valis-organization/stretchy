package com.example.stretchy.features.executetraining.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.Screen
import com.example.stretchy.theme.BananaMania

@Composable
fun TrainingSummaryComposable(numberOfExercises: Int, currentTrainingTime: String, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(BananaMania.toArgb()))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp)
        ) {
            Text(
                text = "You finished your training!",
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(200.dp))
            Text(
                text = "Total Exercises: $numberOfExercises",
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "Time spent: $currentTrainingTime",
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(260.dp))
            Card(
                modifier = Modifier
                    .clickable { navController.navigate(Screen.TrainingListScreen.route) }
                    .height(80.dp)
                    .fillMaxWidth()
                    .padding(12.dp),
                backgroundColor = Color.Black,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp),
                    text = "Finish",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}