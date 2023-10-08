package com.example.stretchy.features.createtraining.ui.composable.widget

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stretchy.R
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.composable.ExerciseNameControls
import com.example.stretchy.features.createtraining.ui.composable.buttons.AddOrEditExerciseButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.AddOrSubtractButtons
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.theme.BananaMania
import com.example.stretchy.theme.Caramel
import com.example.stretchy.theme.Purple500

@Composable
fun ExerciseAndBreakTabsWidget(
    onAddOrEditButtonClick: () -> Unit,
    onTabSizeChange: (activityType: ActivityType) -> Unit,
    onListExerciseHandler: OnListExerciseHandler,
    exerciseWithBreakToEdit: ExercisesWithBreaks
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs =
        listOf(stringResource(id = R.string.exercise), stringResource(id = R.string.exercise_break))

    val doesExerciseExists = exerciseWithBreakToEdit.exercise.name != ""
    val doesBreakExists = exerciseWithBreakToEdit.nextBreakDuration != null

    val currentExerciseWithBreak by remember {
        mutableStateOf(exerciseWithBreakToEdit)
    }
    var isTimelessExercise: Boolean by remember {
        mutableStateOf(exerciseWithBreakToEdit.exercise.duration == 0)
    }
    var isExerciseOrBreakChanged by remember {
        mutableStateOf(
            isExerciseChanged(
                currentExerciseWithBreak.exercise,
                exerciseWithBreakToEdit.exercise
            ) || isBreakChanged(
                currentExerciseWithBreak.nextBreakDuration,
                exerciseWithBreakToEdit.nextBreakDuration
            )
        )
    }

    val context = LocalContext.current

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)

    ) {
        Column(
            Modifier
                .background(color = Color(BananaMania.toArgb()))
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        color = Color(Purple500.toArgb()),
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(text = title, color = Color.Black) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                        },
                        modifier = Modifier.background(Color(Caramel.toArgb()))
                    )
                }
            }

            Column(
                Modifier
                    .padding(start = 12.dp, end = 12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        ExerciseTab(
                            editedExercise = currentExerciseWithBreak.exercise,
                            onNameChange = {
                                currentExerciseWithBreak.exercise.name = it
                                isExerciseOrBreakChanged = isExerciseOrBreakChanged(
                                    currentExerciseWithBreak,
                                    exerciseWithBreakToEdit
                                )
                            },
                            onDurationChange = { currentExerciseWithBreak.exercise.duration = it
                                isExerciseOrBreakChanged = isExerciseOrBreakChanged(
                                    currentExerciseWithBreak,
                                    exerciseWithBreakToEdit
                                )},
                            isTimelessExercise = isTimelessExercise,
                            onCheckboxChange = {
                                isTimelessExercise = !isTimelessExercise
                                if (isTimelessExercise) {
                                    onTabSizeChange(ActivityType.TIMELESS_EXERCISE)
                                } else {
                                    onTabSizeChange(ActivityType.EXERCISE)
                                }
                            }
                        )
                        if (isTimelessExercise) {
                            onTabSizeChange(ActivityType.TIMELESS_EXERCISE)
                        } else {
                            onTabSizeChange(ActivityType.EXERCISE)
                        }
                    }
                    1 -> {
                        BreakTab(
                            breakToEditDuration = currentExerciseWithBreak.nextBreakDuration,
                            onDurationChange = {
                                if (currentExerciseWithBreak.nextBreakDuration == null) {
                                    currentExerciseWithBreak.nextBreakDuration = 0
                                }
                                currentExerciseWithBreak.nextBreakDuration = it
                            },
                        )
                        onTabSizeChange(ActivityType.BREAK)
                    }
                }

                AddOrEditExerciseButton(
                    onClick = {
                        if (currentExerciseWithBreak.exercise.name.isNotEmpty()) {
                            onAddOrEditButtonClick()
                            if (doesExerciseExists) {
                                if (isExerciseOrBreakBeingEdited(
                                        currentExerciseWithBreak,
                                        exerciseWithBreakToEdit,
                                        doesBreakExists
                                    )
                                ) {
                                    onListExerciseHandler.editExercise(currentExerciseWithBreak)
                                    Toast.makeText(
                                        context,
                                        R.string.exercise_edited,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                } else if (isBreakChanged(
                                        currentExerciseWithBreak.nextBreakDuration,
                                        exerciseWithBreakToEdit.nextBreakDuration
                                    ) && currentExerciseWithBreak.nextBreakDuration == 0
                                ) {
                                    onListExerciseHandler.removeBreak(currentExerciseWithBreak.listId)
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                R.string.specify_exercise_name,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    isExerciseOrBreakBeingEdited = doesBreakExists || doesExerciseExists,
                //    isExerciseOrBreakChanged = isExerciseOrBreakChanged,
                )
            }
        }
    }
}

@Composable
private fun ExerciseTab(
    editedExercise: Exercise,
    onNameChange: (name: String) -> Unit,
    onDurationChange: (duration: Int) -> Unit,
    isTimelessExercise: Boolean,
    onCheckboxChange: (isChecked: Boolean) -> Unit //temp
) {
    val sliderMinValue = 1
    val sliderMaxValue = 300
    var exerciseDuration: Int by remember { mutableStateOf(sliderMinValue) }
    var exerciseName: String by remember { mutableStateOf("") }
    var durationBeforeTimelessExerciseSwitch: Int by remember {
        mutableStateOf(
            if (editedExercise.duration == 0) sliderMaxValue
            else editedExercise.duration
        )
    }

    LaunchedEffect(editedExercise) {
        exerciseName = editedExercise.name
        exerciseDuration = editedExercise.duration
    }

    ExerciseNameControls(
        currentName = exerciseName,
        onNameEntered = {
            exerciseName = it
            onNameChange(exerciseName)
        })
    if (isTimelessExercise) {
        TimelessExerciseCheckbox(modifier = Modifier, onClick = {
            exerciseDuration = durationBeforeTimelessExerciseSwitch

            onDurationChange(exerciseDuration)
            onCheckboxChange(it)
        }, isTimelessExercise = true)
    } else {
        Spacer(Modifier.height(4.dp))
        Row(Modifier.height(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier
                    .padding(0.dp),
                text = stringResource(R.string.duration) + " ${
                    toDisplayableLength(
                        exerciseDuration
                    )
                }",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            TimelessExerciseCheckbox(modifier = Modifier.padding(top = 4.dp), onClick = {
                exerciseDuration = 0
                onDurationChange(exerciseDuration)
                onCheckboxChange(it)
            }, isTimelessExercise = false)
        }

        Slider(
            value = exerciseDuration.toFloat(),
            onValueChange = {
                durationBeforeTimelessExerciseSwitch = editedExercise.duration
                exerciseDuration = it.toInt()
                onDurationChange(exerciseDuration)
            },
            valueRange = sliderMinValue.toFloat()..sliderMaxValue.toFloat(),
            modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
        )
        AddOrSubtractButtons { changeValue ->
            if (exerciseDuration + changeValue in sliderMinValue..300) {
                exerciseDuration += changeValue
                onDurationChange(exerciseDuration)
            }
        }
    }
}

@Composable
private fun BreakTab(
    breakToEditDuration: Int?,
    onDurationChange: (duration: Int) -> Unit,
) {
    val sliderMinValue = 0
    val sliderMaxValue = 300
    var breakDuration: Int by remember { mutableStateOf(sliderMinValue) }
    LaunchedEffect(breakToEditDuration) {
        if (breakToEditDuration != null) {
            breakDuration = breakToEditDuration
        }
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        text = stringResource(R.string.duration) + " ${
            toDisplayableLength(
                breakDuration
            )
        }",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Slider(
        value = breakDuration.toFloat(),
        onValueChange = {
            breakDuration = it.toInt()
        },
        valueRange = sliderMinValue.toFloat()..sliderMaxValue.toFloat(),
    )
    AddOrSubtractButtons { changeValue ->
        if (breakDuration + changeValue in sliderMinValue..sliderMaxValue) {
            breakDuration += changeValue
            onDurationChange(breakDuration)
        }
    }
}

private fun isExerciseChanged(currentExercise: Exercise, exerciseToEdit: Exercise) =
    currentExercise.name != exerciseToEdit.name || currentExercise.duration != exerciseToEdit.duration

private fun isBreakChanged(currentBreakDuration: Int?, breakToEditDuration: Int?) =
    currentBreakDuration != breakToEditDuration

fun isExerciseOrBreakChanged(
    currentExerciseWithBreak: ExercisesWithBreaks,
    exerciseWithBreakToEdit: ExercisesWithBreaks
) = isExerciseChanged(
    currentExerciseWithBreak.exercise,
    exerciseWithBreakToEdit.exercise
) || isBreakChanged(
    currentExerciseWithBreak.nextBreakDuration,
    exerciseWithBreakToEdit.nextBreakDuration
)

private fun isBreakInitialized(currentBreakDuration: Int?) =
    currentBreakDuration != 0 && currentBreakDuration != null

private fun isExerciseOrBreakBeingEdited(
    currentExercise: ExercisesWithBreaks,
    exerciseToEdit: ExercisesWithBreaks,
    doesBreakExists: Boolean
): Boolean {
    return isExerciseChanged(
        currentExercise.exercise,
        exerciseToEdit.exercise
    ) || (!doesBreakExists && isBreakInitialized(
        currentExercise.nextBreakDuration
    )) || (isBreakInitialized(currentExercise.nextBreakDuration) && isBreakChanged(
        currentExercise.nextBreakDuration,
        exerciseToEdit.nextBreakDuration
    )
            )
}

fun toDisplayableLength(exerciseDuration: Int): String {
    return if (exerciseDuration >= 60) {
        val mins = exerciseDuration / 60
        val rest = exerciseDuration.mod(60)
        "$mins min $rest sec"
    } else {
        "$exerciseDuration sec"
    }
}

@Composable
private fun TimelessExerciseCheckbox(
    modifier: Modifier = Modifier,
    onClick: (isChecked: Boolean) -> Unit,
    isTimelessExercise: Boolean
) {
    var isChecked by remember { mutableStateOf(isTimelessExercise) }

    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onClick(isChecked)
            },
            modifier = Modifier.padding(0.dp)
        )

        Text(
            text = "TimelessExercise",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
        )
    }
}
