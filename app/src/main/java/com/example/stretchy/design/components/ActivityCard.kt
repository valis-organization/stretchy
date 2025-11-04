package com.example.stretchy.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Whatshot
import com.example.stretchy.database.data.TrainingType

enum class ActivityCardState { Normal, Draft }

@Composable
fun ActivityCard(
    title: String,
    setsCount: Int,
    durationMinutes: Int,
    streakCount: Int,
    lastExercised: String,
    modifier: Modifier = Modifier,
    state: ActivityCardState = ActivityCardState.Normal,
    trainingType: TrainingType = TrainingType.STRETCH,
    colorIndex: Int = 0, // New parameter to alternate colors
    onClick: () -> Unit = {}
) {
    Box(modifier = modifier) {
        // Draft badge positioned above the card
        if (state == ActivityCardState.Draft) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Draft",
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Main card content with accent line
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Row {
                // Left accent line with colors based on training type
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(
                            color = when {
                                state == ActivityCardState.Draft -> Color(0xFFF44336) // Red for draft
                                trainingType == TrainingType.STRETCH -> {
                                    if (colorIndex % 2 == 0) Color(0xFF4CAF50) else Color(0xFF66BB6A) // Green shades
                                }
                                trainingType == TrainingType.BODYWEIGHT -> {
                                    if (colorIndex % 2 == 0) Color(0xFFFF8A65) else Color(0xFFFF5722) // Orange/Red shades
                                }
                                else -> Color(0xFF4CAF50) // Default green
                            }
                        )
                )

                // Card content
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxHeight(), // Fill available height from row
                    verticalArrangement = Arrangement.SpaceBetween // Title to top, metrics to bottom
                ) {
                    // Top: Title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Bottom: Metrics (always at bottom) with margin from title
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text(text = setsCount.toString(), style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Filled.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text(text = "${durationMinutes}m", style = MaterialTheme.typography.bodySmall)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Filled.Whatshot, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text(text = streakCount.toString(), style = MaterialTheme.typography.bodySmall)
                            Text(text = " • ")
                            Text(text = lastExercised, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityCardSimplePreview() {
    DesignTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityCard(title = "Deep Hip Openers", setsCount = 7, durationMinutes = 12, streakCount = 15, lastExercised = "2 days ago", trainingType = TrainingType.STRETCH, colorIndex = 0)
            ActivityCard(title = "Quick Energy Boost", setsCount = 5, durationMinutes = 5, streakCount = 31, lastExercised = "today", trainingType = TrainingType.BODYWEIGHT, colorIndex = 1)
            ActivityCard(title = "Draft Exercise", setsCount = 8, durationMinutes = 10, streakCount = 0, lastExercised = "never", state = ActivityCardState.Draft, trainingType = TrainingType.STRETCH, colorIndex = 0)
        }
    }
}
