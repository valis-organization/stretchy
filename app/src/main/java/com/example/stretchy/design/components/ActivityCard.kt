package com.example.stretchy.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
    onClick: () -> Unit = {},
    onCopy: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var isSelected by remember { mutableStateOf(false) }
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

        // Action buttons when selected (positioned above the card)
        if (isSelected) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(2f)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = {
                        onCopy()
                        isSelected = false
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                IconButton(
                    onClick = {
                        onEdit()
                        isSelected = false
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                IconButton(
                    onClick = {
                        onDelete()
                        isSelected = false
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Main card content with accent line
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (isSelected) {
                                isSelected = false
                            } else {
                                onClick()
                            }
                        },
                        onLongPress = {
                            isSelected = !isSelected
                        }
                    )
                },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 8.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    state == ActivityCardState.Draft -> Color(0xFFFFEBEE)
                    isSelected -> Color(0xFFE8F5E8) // Greyish green for selected
                    else -> Color.White
                }
            )
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
                        // 4-column layout with variable spacing
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            // Column 1: Count and Fire icons
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Count icon with circle background
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            color = when (trainingType) {
                                                TrainingType.STRETCH -> Color(0xFF4CAF50)
                                                TrainingType.BODYWEIGHT -> Color(0xFFFF9800)
                                            }.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.List,
                                        contentDescription = null,
                                        tint = when (trainingType) {
                                            TrainingType.STRETCH -> Color(0xFF4CAF50)
                                            TrainingType.BODYWEIGHT -> Color(0xFFFF9800)
                                        },
                                        modifier = Modifier.size(12.dp)
                                    )
                                }

                                // Fire icon - red only when streak > 0
                                Box(
                                    modifier = Modifier.size(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Whatshot,
                                        contentDescription = null,
                                        tint = if (streakCount > 0) Color(0xFFFF5722) else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Column 2: Count and Streak texts (smaller spacing from column 1)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = setsCount.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                    text = streakCount.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }

                            // Column 3: Time icon + calendar (normal spacing from column 2)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(
                                            color = when (trainingType) {
                                                TrainingType.STRETCH -> Color(0xFF4CAF50)
                                                TrainingType.BODYWEIGHT -> Color(0xFFFF9800)
                                            }.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AccessTime,
                                        contentDescription = null,
                                        tint = when (trainingType) {
                                            TrainingType.STRETCH -> Color(0xFF4CAF50)
                                            TrainingType.BODYWEIGHT -> Color(0xFFFF9800)
                                        },
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier.size(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            // Column 4: Time and last exercised (smaller spacing from column 3)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "${durationMinutes}m",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                    text = lastExercised,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 600)
@Composable
private fun ActivityCardPreview() {
    DesignTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Normal stretch card with active streak
                ActivityCard(
                    title = "Deep Hip Openers",
                    setsCount = 7,
                    durationMinutes = 12,
                    streakCount = 15,
                    lastExercised = "2 days ago",
                    trainingType = TrainingType.STRETCH,
                    colorIndex = 0,
                    modifier = Modifier.fillMaxWidth()
                )

                // Bodyweight card with zero streak
                ActivityCard(
                    title = "Push-up Challenge",
                    setsCount = 5,
                    durationMinutes = 8,
                    streakCount = 0,
                    lastExercised = "1 week ago",
                    trainingType = TrainingType.BODYWEIGHT,
                    colorIndex = 1,
                    modifier = Modifier.fillMaxWidth()
                )

                // Draft card with long title
                ActivityCard(
                    title = "Advanced Full Body Stretching Routine for Better Flexibility and Mobility",
                    setsCount = 12,
                    durationMinutes = 25,
                    streakCount = 0,
                    lastExercised = "never",
                    state = ActivityCardState.Draft,
                    trainingType = TrainingType.STRETCH,
                    colorIndex = 0,
                    modifier = Modifier.fillMaxWidth()
                )

                // Short title with high streak
                ActivityCard(
                    title = "Quick",
                    setsCount = 3,
                    durationMinutes = 5,
                    streakCount = 45,
                    lastExercised = "today",
                    trainingType = TrainingType.BODYWEIGHT,
                    colorIndex = 0,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480, name = "All States Grid")
@Composable
private fun ActivityCardAllStatesPreview() {
    DesignTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row 1: Normal vs Selected
                Text(
                    text = "Normal vs Selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActivityCard(
                        title = "Normal Card",
                        setsCount = 5,
                        durationMinutes = 10,
                        streakCount = 7,
                        lastExercised = "today",
                        trainingType = TrainingType.STRETCH,
                        modifier = Modifier.weight(1f)
                    )

                    // Simulated selected card
                    Box(modifier = Modifier.weight(1f)) {
                        // Action buttons overlay
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .zIndex(2f)
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE8F5E8)
                            )
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .fillMaxHeight()
                                        .background(color = Color(0xFF4CAF50))
                                )
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Selected",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                                    )
                                    Text(
                                        text = "5 • 10m",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Row 2: Training types
                Text(
                    text = "Training Types",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActivityCard(
                        title = "Stretch",
                        setsCount = 8,
                        durationMinutes = 15,
                        streakCount = 12,
                        lastExercised = "today",
                        trainingType = TrainingType.STRETCH,
                        modifier = Modifier.weight(1f)
                    )
                    ActivityCard(
                        title = "Bodyweight",
                        setsCount = 6,
                        durationMinutes = 20,
                        streakCount = 5,
                        lastExercised = "yesterday",
                        trainingType = TrainingType.BODYWEIGHT,
                        colorIndex = 1,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Special states
                Text(
                    text = "Special States",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ActivityCard(
                    title = "Draft Exercise with Very Long Title That Should Wrap",
                    setsCount = 10,
                    durationMinutes = 30,
                    streakCount = 0,
                    lastExercised = "never",
                    state = ActivityCardState.Draft,
                    trainingType = TrainingType.STRETCH,
                    modifier = Modifier.fillMaxWidth()
                )

                // Row 4: Streak states
                Text(
                    text = "Streak States",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActivityCard(
                        title = "No Streak",
                        setsCount = 3,
                        durationMinutes = 5,
                        streakCount = 0,
                        lastExercised = "2 weeks ago",
                        trainingType = TrainingType.BODYWEIGHT,
                        modifier = Modifier.weight(1f)
                    )
                    ActivityCard(
                        title = "High Streak",
                        setsCount = 4,
                        durationMinutes = 8,
                        streakCount = 99,
                        lastExercised = "today",
                        trainingType = TrainingType.STRETCH,
                        colorIndex = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 280, heightDp = 140, name = "Compact Card")
@Composable
private fun ActivityCardCompactPreview() {
    DesignTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ActivityCard(
                title = "Compact View",
                setsCount = 4,
                durationMinutes = 7,
                streakCount = 12,
                lastExercised = "yesterday",
                trainingType = TrainingType.STRETCH,
                colorIndex = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

