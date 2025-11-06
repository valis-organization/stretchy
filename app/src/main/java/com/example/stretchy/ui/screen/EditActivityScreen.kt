package com.example.stretchy.ui.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.example.stretchy.design.components.StretchingTheme

// Compact exercise widget state
data class ExerciseWidgetState(
    val id: Int,
    val title: String,
    val selectedTimeSeconds: Int = 30,
    val availableTimes: List<Int> = listOf(1, 30, 45, 60, 90),
    val customTimeSeconds: Float = 30f,
    val isTimelessExercise: Boolean = false,
    val breakTimeSeconds: Int = 10,
    val availableBreakTimes: List<Int> = listOf(5, 10, 15, 30, 60),
    val customBreakTimeSeconds: Float = 10f,
    val isBreakExpanded: Boolean = false,
    val isExpanded: Boolean = false,
    val accentColor: Color = Color(0xFF4CAF50)
)

@Composable
fun ExerciseWidget(
    state: ExerciseWidgetState,
    onStateChange: (ExerciseWidgetState) -> Unit,
    modifier: Modifier = Modifier,
    numberCircleSize: Dp = 18.5.dp,
    onDelete: (() -> Unit)? = null,
    onEditName: (() -> Unit)? = null) {
    // constrain card width so it doesn't stretch full width and center it via parent Box
    Card(
        modifier = modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .widthIn(max = 340.dp)
            .wrapContentHeight()
            .clickable {
                onStateChange(state.copy(
                    isExpanded = !state.isExpanded,
                    isBreakExpanded = if (!state.isExpanded) false else state.isBreakExpanded // Collapse break section when expanding exercise
                ))
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.wrapContentWidth().height(IntrinsicSize.Min)) {
            // left accent line
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(state.accentColor))
            // content
            Column(modifier = Modifier.padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    // drag handle (six dots)
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = "Reorder",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // colored number circle
                    Box(
                        modifier = Modifier
                            .size(numberCircleSize)
                            .background(color = state.accentColor.copy(alpha = 0.12f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.id.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = state.accentColor,
                            maxLines = 1,
                            lineHeight = 10.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = state.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (onEditName != null) {
                                    Modifier.clickable { onEditName() }
                                } else Modifier
                            )
                    )

                    // Delete button (only show when onDelete is provided)
                    onDelete?.let { deleteCallback ->
                        IconButton(
                            onClick = deleteCallback,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Exercise",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (state.isExpanded) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            state.availableTimes.forEach { time ->
                                TimeButton(
                                    isSelected = time == state.customTimeSeconds.toInt() && !state.isTimelessExercise,
                                    onClick = {
                                        onStateChange(state.copy(selectedTimeSeconds = time, customTimeSeconds = time.toFloat(), isTimelessExercise = false))
                                    },
                                    modifier = Modifier
                                        .height(24.dp)
                                        .defaultMinSize(minWidth = 36.dp),
                                    time = time
                                )
                            }

                            TimeButton(
                                isSelected = state.isTimelessExercise,
                                onClick = {
                                    onStateChange(state.copy(isTimelessExercise = !state.isTimelessExercise))
                                },
                                modifier = Modifier
                                    .height(24.dp)
                                    .defaultMinSize(minWidth = 36.dp),
                                isIcon = true
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!state.isTimelessExercise) {
                            Column(modifier = Modifier.height(10.dp)) {
                                Slider(value = state.customTimeSeconds, onValueChange = { new ->
                                    val rounded = new.toInt()
                                    onStateChange(state.copy(customTimeSeconds = new, selectedTimeSeconds = rounded))
                                }, valueRange = 1f..120f, modifier = Modifier.fillMaxWidth(),
                                    colors = SliderDefaults.colors(thumbColor = state.accentColor, activeTrackColor = state.accentColor,
                                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                } else {
                    // collapsed: show time with clock icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "${state.selectedTimeSeconds}s", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // subtle separator between main area and break
                HorizontalDivider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp), color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

                // break row with coffee icon - clickable to expand break settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onStateChange(state.copy(
                                isBreakExpanded = !state.isBreakExpanded,
                                isExpanded = if (!state.isBreakExpanded) false else state.isExpanded // Collapse exercise section when expanding break
                            ))
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = "Break",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${state.breakTimeSeconds}s break", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // break time selection when expanded
                if (state.isBreakExpanded) {
                    Spacer(modifier = Modifier.height(0.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            state.availableBreakTimes.forEach { time ->
                                TimeButton(
                                    isSelected = time == state.customBreakTimeSeconds.toInt(),
                                    onClick = {
                                        onStateChange(state.copy(
                                            breakTimeSeconds = time,
                                            customBreakTimeSeconds = time.toFloat()
                                        ))
                                    },
                                    modifier = Modifier
                                        .height(24.dp)
                                        .defaultMinSize(minWidth = 36.dp),
                                    time = time
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.height(10.dp)) {
                            Slider(
                                value = state.customBreakTimeSeconds,
                                onValueChange = { new ->
                                    val rounded = new.toInt()
                                    onStateChange(state.copy(
                                        customBreakTimeSeconds = new,
                                        breakTimeSeconds = rounded
                                    ))
                                },
                                valueRange = 1f..120f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = state.accentColor,
                                    activeTrackColor = state.accentColor,
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    time: Int? = null,
    isIcon: Boolean = false
) {
    // slightly darker primary for stronger selected visual
    val primary = MaterialTheme.colorScheme.primary
    val darken = 0.78f
    val darkerPrimary = Color(
        (primary.red * darken).coerceIn(0f, 1f),
        (primary.green * darken).coerceIn(0f, 1f),
        (primary.blue * darken).coerceIn(0f, 1f),
        primary.alpha
    )

    val bg = if (isSelected) darkerPrimary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) {
        if (bg.luminance() < 0.5f) Color.White else Color.Black
    } else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 8.dp), // only horizontal padding; vertical centering via fixed height
        contentAlignment = Alignment.Center
    ) {
        if (isIcon) {
            Icon(
                imageVector = Icons.Default.AllInclusive,
                contentDescription = "Timeless",
                tint = contentColor,
                modifier = Modifier.size(16.dp).align(Alignment.Center)
            )
        } else {
            Text(
                text = "${time}s",
                fontSize = 10.sp,
                maxLines = 1,
                color = contentColor,
                modifier = Modifier.align(Alignment.Center),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun EditActivityScreen(modifier: Modifier = Modifier) {
    var exercises by remember {
        mutableStateOf(
            listOf(
                ExerciseWidgetState(id = 1, title = "Neck Rolls", selectedTimeSeconds = 30, customTimeSeconds = 30f, isTimelessExercise = false, breakTimeSeconds = 10, customBreakTimeSeconds = 10f, isBreakExpanded = false, isExpanded = false, accentColor = Color(0xFF4CAF50)),
                ExerciseWidgetState(id = 2, title = "Shoulder Stretch", selectedTimeSeconds = 60, customTimeSeconds = 60f, isTimelessExercise = false, breakTimeSeconds = 15, customBreakTimeSeconds = 15f, isBreakExpanded = false, isExpanded = true, accentColor = Color(0xFF66BB6A)),
                ExerciseWidgetState(id = 3, title = "Deep Breathing", selectedTimeSeconds = 45, customTimeSeconds = 45f, isTimelessExercise = true, breakTimeSeconds = 5, customBreakTimeSeconds = 5f, isBreakExpanded = true, isExpanded = false, accentColor = Color(0xFF81C784))
            )
        )
    }
    Column(modifier = modifier.wrapContentWidth().wrapContentHeight().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        exercises.forEachIndexed { i, ex ->
            Box(modifier = Modifier.wrapContentWidth().wrapContentHeight(), contentAlignment = Alignment.Center) {
                ExerciseWidget(state = ex, onStateChange = { new -> exercises = exercises.toMutableList().apply { this[i] = new } }, numberCircleSize = 18.5.dp)
            }
        }
    }
}

@Preview(showBackground = true, name = "List of Widgets")
@Composable
fun EditActivityListPreview() {
    // Use app theme so primaryContainer/onPrimaryContainer are picked from DesignTheme
    StretchingTheme {
        // Constrain preview width and allow height to wrap content so it looks compact
        Box(modifier = Modifier.width(340.dp).wrapContentHeight()) {
            EditActivityScreen(modifier = Modifier.width(340.dp).wrapContentHeight())
        }
    }
}
