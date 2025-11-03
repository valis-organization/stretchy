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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


data class ActivityItem(
    val title: String,
    val sets: Int,
    val minutes: Int,
    val calories: Int,
    val dateLabel: String,
    val isDraft: Boolean = false
)

@Composable
fun ActivityListScreen(
    activities: List<ActivityItem>,
    modifier: Modifier = Modifier,
    onAdd: () -> Unit = {},
    onFilter: () -> Unit = {},
    onSearch: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
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
                            calories = item.calories,
                            dateLabel = item.dateLabel,
                            state = if (item.isDraft) ActivityCardState.Draft else ActivityCardState.Normal,
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
            item { Spacer(modifier = Modifier.padding(bottom = 72.dp)) }
        }

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
                    .fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onFilter) { Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter") }
                    IconButton(onClick = onSearch) { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") }
                }
                FloatingActionButton(onClick = onAdd) { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityListScreenPreview() {
    DesignTheme(darkTheme = false) {
        val demo = listOf(
            // Row 1: Short titles
            ActivityItem("Upper Body", 8, 6, 8, "8m"),
            ActivityItem("Lower Body", 9, 8, 9, "10m"),

            // Row 2: Long + short with draft
            ActivityItem("Complete Neck & Shoulders Relief Program", 6, 4, 6, "6m"),
            ActivityItem("Yoga Flow Stretch", 6, 10, 20, "20m", isDraft = true),

            // Row 3: Mixed
            ActivityItem("Dynamic", 5, 6, 10, "6m"),
            ActivityItem("Post-Workout Cool Down and Recovery Session", 6, 9, 14, "9m"),

            // Row 4: Draft + normal
            ActivityItem("Hamstring Focus", 5, 12, 11, "12m", isDraft = true),
            ActivityItem("Spine Alignment", 6, 8, 10, "14m")
        )
        ActivityListScreen(activities = demo)
    }
}
