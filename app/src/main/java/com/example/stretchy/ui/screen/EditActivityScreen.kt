package com.example.stretchy.ui.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Compact exercise widget state
data class ExerciseWidgetState(
    val id: Int,
    val title: String,
    val selectedTimeSeconds: Int = 30,
    val availableTimes: List<Int> = listOf(1, 30, 45, 60, 90),
    val customTimeSeconds: Float = 30f,
    val isTimelessExercise: Boolean = false,
    val breakPercentage: Int = 1,
    val isExpanded: Boolean = false,
    val accentColor: Color = Color(0xFF4CAF50)
)
@Composable
fun ExerciseWidget(
    state: ExerciseWidgetState,
    onStateChange: (ExerciseWidgetState) -> Unit,
    modifier: Modifier = Modifier
) {
    // constrain card width so it doesn't stretch full width and center it via parent Box
    Card(
        modifier = modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .widthIn(max = 340.dp)
            .wrapContentHeight()
            .clickable { onStateChange(state.copy(isExpanded = !state.isExpanded)) },
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
                    Box(
                        modifier = Modifier.size(18.dp).background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            shape = CircleShape
                        ), contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.id.toString(), fontSize = 10.sp, fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = state.title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    // show timeless icon if collapsed and timeless
                    if (!state.isExpanded && state.isTimelessExercise) {
                        Icon(imageVector = Icons.Default.AllInclusive, contentDescription = "Timeless",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (state.isExpanded) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            state.availableTimes.forEach { time ->
                                TimeButton(
                                    time = time,
                                    isSelected = time == state.customTimeSeconds.toInt() && !state.isTimelessExercise,
                                    onClick = {
                                        onStateChange(state.copy(selectedTimeSeconds = time, customTimeSeconds = time.toFloat(), isTimelessExercise = false))
                                    },
                                    modifier = Modifier
                                        .height(30.dp)
                                        .defaultMinSize(minWidth = 36.dp)
                                )
                            }
                            // timeless toggle
                            IconButton(onClick = { onStateChange(state.copy(isTimelessExercise = !state.isTimelessExercise)) },
                                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(
                                    color = if (state.isTimelessExercise) state.accentColor else MaterialTheme.colorScheme.surfaceVariant)) {
                                Icon(imageVector = Icons.Default.AllInclusive, contentDescription = "Timeless",
                                    tint = if (state.isTimelessExercise) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (!state.isTimelessExercise) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "${state.customTimeSeconds.toInt()}s", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.CenterHorizontally))
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${state.selectedTimeSeconds}s", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(text = "${state.breakPercentage}% break", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
@Composable
private fun TimeButton(time: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(bg).clickable { onClick() }.padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
        Text(text = "${time}s", fontSize = 10.sp, color = textColor)
    }
}
@Composable
fun EditActivityScreen(modifier: Modifier = Modifier) {
    var exercises by remember {
        mutableStateOf(
            listOf(
                ExerciseWidgetState(id = 1, title = "Neck Rolls", selectedTimeSeconds = 30, customTimeSeconds = 30f, isTimelessExercise = false, breakPercentage = 1, isExpanded = false, accentColor = Color(0xFF4CAF50)),
                ExerciseWidgetState(id = 2, title = "Shoulder Stretch", selectedTimeSeconds = 60, customTimeSeconds = 60f, isTimelessExercise = false, breakPercentage = 1, isExpanded = true, accentColor = Color(0xFF66BB6A)),
                ExerciseWidgetState(id = 3, title = "Deep Breathing", selectedTimeSeconds = 45, customTimeSeconds = 45f, isTimelessExercise = true, breakPercentage = 0, isExpanded = false, accentColor = Color(0xFF81C784))
            )
        )
    }
    Column(modifier = modifier.wrapContentWidth().wrapContentHeight().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        exercises.forEachIndexed { i, ex ->
            Box(modifier = Modifier.wrapContentWidth().wrapContentHeight(), contentAlignment = Alignment.Center) {
                ExerciseWidget(state = ex, onStateChange = { new -> exercises = exercises.toMutableList().apply { this[i] = new } })
            }
        }
    }
}
@Preview(showBackground = true, name = "List of Widgets")
@Composable
fun EditActivityListPreview() {
    MaterialTheme {
        // Constrain preview width and allow height to wrap content so it looks compact
        Box(modifier = Modifier.width(340.dp).wrapContentHeight()) {
            EditActivityScreen(modifier = Modifier.width(340.dp).wrapContentHeight())
        }
    }
}
