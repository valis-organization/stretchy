package com.example.stretchy.design.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stretchy.design.components.*

@Composable
fun ThemePreviewContent() {
    val colors = LocalDesignColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors.backgroundGradient)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Theme Preview",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // Primary color card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.cardSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Primary Colors",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.metricTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.accentStart, RoundedCornerShape(8.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.accentEnd, RoundedCornerShape(8.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.backgroundDark, RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            // Bottom bar and floating button colors
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.cardSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "UI Element Colors",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.metricTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.bottomBarBackground, RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = "Bottom Bar",
                            color = colors.metricTextSecondary
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.floatingButtonBackground, RoundedCornerShape(20.dp))
                        )
                        Text(
                            text = "FAB",
                            color = colors.metricTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Stretching Theme")
@Composable
private fun StretchingThemePreview() {
    StretchingTheme {
        ThemePreviewContent()
    }
}

@Preview(name = "Training Theme")
@Composable
private fun TrainingThemePreview() {
    TrainingTheme {
        ThemePreviewContent()
    }
}
