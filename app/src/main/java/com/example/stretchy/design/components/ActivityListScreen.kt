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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stretchy.R
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
    modifier: Modifier = Modifier,
    onAdd: () -> Unit = {},
    onFilter: () -> Unit = {},
    onSearch: () -> Unit = {},
    onActivityClick: (ActivityItem) -> Unit = {},
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onPerformExport: () -> Unit = {},
    onPerformImport: suspend () -> Unit = {}
) {
    ActivityListView(
        activities = activities,
        modifier = modifier,
        onAdd = onAdd,
        onFilter = onFilter,
        onSearch = onSearch,
        onActivityClick = onActivityClick,
        onExportClick = onExportClick,
        onImportClick = onImportClick,
        onPerformExport = onPerformExport,
        onPerformImport = onPerformImport
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListView(
    activities: List<ActivityItem>,
    modifier: Modifier = Modifier,
    onAdd: () -> Unit = {},
    onFilter: () -> Unit = {},
    onSearch: () -> Unit = {},
    onActivityClick: (ActivityItem) -> Unit = {},
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onPerformExport: () -> Unit = {},
    onPerformImport: suspend () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    Menu(
                        onRequestExportPermission = onExportClick,
                        onRequestImportPermission = onImportClick,
                        onPerformExport = onPerformExport,
                        onPerformImport = onPerformImport
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
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
                        colors = listOf(colors.primary.copy(alpha = 0.08f), colors.secondary.copy(alpha = 0.02f))
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val rows = activities.chunked(2)
                itemsIndexed(rows) { _, rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) // Row height equals tallest child
                    ) {
                        for (item in rowItems) {
                            ActivityCard(
                                title = item.title,
                                setsCount = item.sets,
                                durationMinutes = item.minutes,
                                streakCount = item.streakCount,
                                lastExercised = item.lastExercised,
                                state = if (item.isDraft) ActivityCardState.Draft else ActivityCardState.Normal,
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

            // Bottom floating search/filter bar
            Surface(
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth(0.7f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onFilter) { Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter") }
                    IconButton(onClick = onSearch) { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityListScreenPreview() {
    DesignTheme(darkTheme = false) {
        val demo = listOf(
            ActivityItem("Upper Body Release", 8, 6, 3, "2d ago"),
            ActivityItem("Lower Body Mobility", 9, 8, 5, "1d ago"),
            ActivityItem("Complete Neck & Shoulders Relief Program", 6, 4, 2, "3d ago"),
            ActivityItem("Yoga Flow Stretch", 6, 10, 7, "today", isDraft = true),
            ActivityItem("Dynamic", 5, 6, 1, "1w ago"),
            ActivityItem("Post-Workout Cool Down and Recovery Session", 6, 9, 4, "2d ago"),
            ActivityItem("Hamstring Focus", 5, 12, 8, "today", isDraft = true),
            ActivityItem("Spine Alignment", 6, 8, 3, "3d ago")
        )
        ActivityListScreen(activities = demo)
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityListViewPreview() {
    DesignTheme(darkTheme = false) {
        val demo = listOf(
            ActivityItem("Upper Body", 8, 6, 3, "2d ago"),
            ActivityItem("Lower Body", 9, 8, 5, "1d ago"),
            ActivityItem("Yoga Flow Stretch", 6, 10, 7, "today", isDraft = true),
            ActivityItem("Dynamic Warm-up", 5, 6, 1, "1w ago")
        )
        ActivityListView(activities = demo)
    }
}
