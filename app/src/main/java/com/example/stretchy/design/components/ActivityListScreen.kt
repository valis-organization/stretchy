package com.example.stretchy.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.traininglist.ui.composable.Menu
import com.example.stretchy.features.traininglist.ui.data.Training

data class ActivityItem(
    val title: String,
    val sets: Int, // numberOfExercises from Training
    val minutes: Int, // timeInSeconds converted to minutes from Training
    val streakCount: Int, // streak count (new field)
    val lastExercised: String, // last exercised time (new field)
    val isDraft: Boolean = false
)

// Extension function to convert Training to ActivityItem
fun Training.toActivityItem(streakCount: Int = 0, lastExercised: String = "today"): ActivityItem {
    return ActivityItem(
        title = name,
        sets = numberOfExercises,
        minutes = timeInSeconds / 60, // convert seconds to minutes
        streakCount = streakCount,
        lastExercised = lastExercised,
        isDraft = false // can be determined based on some Training property if needed
    )
}

@Composable
fun ActivityListScreen(
    activities: List<ActivityItem>,
    trainingType: TrainingType,
    modifier: Modifier = Modifier,
    onAdd: () -> Unit = {},
    onActivityClick: (ActivityItem) -> Unit = {},
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onPerformExport: () -> Unit = {},
    onPerformImport: suspend () -> Unit = {}
) {
    ActivityListView(
        activities = activities,
        trainingType = trainingType,
        modifier = modifier,
        onAdd = onAdd,
        onActivityClick = onActivityClick,
        onExportClick = onExportClick,
        onImportClick = onImportClick,
        onPerformExport = onPerformExport,
        onPerformImport = onPerformImport
    )
}

@Composable
fun ActivityListView(
    activities: List<ActivityItem>,
    trainingType: TrainingType,
    modifier: Modifier = Modifier,
    onAdd: () -> Unit = {},
    onActivityClick: (ActivityItem) -> Unit = {},
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onPerformExport: () -> Unit = {},
    onPerformImport: suspend () -> Unit = {}
) {
    // Define colors based on training type
    val (primaryColor, secondaryColor) = when (trainingType) {
        TrainingType.STRETCH -> Pair(Color(0xFF4CAF50), Color(0xFF66BB6A)) // Green colors
        TrainingType.BODYWEIGHT -> Pair(Color(0xFFFF8A65), Color(0xFFFF5722)) // Orange/Red colors
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = LocalDesignColors.current.floatingButtonBackground,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.08f), secondaryColor.copy(alpha = 0.02f))
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Custom header with title and import/export icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Menu(
                        onRequestExportPermission = onExportClick,
                        onRequestImportPermission = onImportClick,
                        onPerformExport = onPerformExport,
                        onPerformImport = onPerformImport
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                val rows = activities.chunked(2)
                itemsIndexed(rows) { rowIndex, rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) // Row height equals tallest child
                    ) {
                        rowItems.forEachIndexed { itemIndex, item ->
                            ActivityCard(
                                title = item.title,
                                setsCount = item.sets,
                                durationMinutes = item.minutes,
                                streakCount = item.streakCount,
                                lastExercised = item.lastExercised,
                                state = if (item.isDraft) ActivityCardState.Draft else ActivityCardState.Normal,
                                trainingType = trainingType,
                                colorIndex = (rowIndex * 2) + itemIndex, // Calculate global index for alternating colors
                                onClick = { onActivityClick(item) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight() // Fill the row height
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                item { Spacer(modifier = Modifier.padding(bottom = 16.dp)) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityListScreenStretchPreview() {
    DesignTheme(darkTheme = false) {
        val demo = listOf(
            ActivityItem("Upper Body Release", 8, 6, 3, "2d ago"),
            ActivityItem("Lower Body Mobility", 9, 8, 5, "1d ago"),
            ActivityItem("Complete Neck & Shoulders Relief Program", 6, 4, 2, "3d ago"),
            ActivityItem("Yoga Flow Stretch", 6, 10, 7, "today", isDraft = true)
        )
        ActivityListScreen(activities = demo, trainingType = TrainingType.STRETCH)
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityListScreenBodyweightPreview() {
    DesignTheme(darkTheme = false) {
        val demo = listOf(
            ActivityItem("Deep Hip Openers", 7, 12, 15, "2 days ago"),
            ActivityItem("Quick Energy Boost", 5, 5, 31, "today"),
            ActivityItem("HIIT Workout", 6, 10, 7, "today", isDraft = true),
            ActivityItem("Dynamic Warm-up", 5, 6, 1, "1w ago")
        )
        ActivityListScreen(activities = demo, trainingType = TrainingType.BODYWEIGHT)
    }
}
