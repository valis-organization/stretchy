package com.example.stretchy.features.executetraining.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stretchy.features.executetraining.sound.SoundPlayer
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel

@Composable
fun ExecuteTrainingScreen(
    viewModel: ExecuteTrainingViewModel,
    soundPlayer: SoundPlayer,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Execute Training Screen", color = Color.White, fontSize = 24.sp)
    }
}
