package com.example.stretchy.features.createtraining.ui.composable.widget

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.composable.ExerciseNameControls
import com.example.stretchy.features.createtraining.ui.composable.buttons.AddOrEditExerciseButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.AddOrSubtractButtons
import com.example.stretchy.features.createtraining.ui.data.BreakAfterExercise
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity
import com.example.stretchy.theme.BananaMania
import com.example.stretchy.theme.Caramel
import com.example.stretchy.theme.Purple500

@Composable
fun CreateExerciseWidget(
    viewModel: CreateOrEditTrainingViewModel,
    exerciseToEdit: Exercise,
    breakToEdit: BreakAfterExercise?,
    widgetVisible: Boolean,
    onAddOrEditButtonClick: () -> Unit,
    trainingType: TrainingType,
    isAutoBreakClicked: Boolean
) {
    AnimatedVisibility(visible = !widgetVisible) {
        AddExerciseWidget {
            onAddOrEditButtonClick()
        }
    }
    AnimatedVisibility(
        visible = widgetVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        ExerciseAndBreakTabsWidget(
            viewModel,
            exerciseToEdit,
            breakToEdit,
            onAddOrEditButtonClick,
            trainingType,
            isAutoBreakClicked
        )
    }
}

@Composable
private fun AddExerciseWidget(onAddOrEditButtonClick: () -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color = Color(BananaMania.toArgb()))
                .clickable {
                    onAddOrEditButtonClick()
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.desc_plus_icon)
            )
        }
    }
}

@Composable
private fun ExerciseAndBreakTabsWidget(
    viewModel: CreateOrEditTrainingViewModel,
    exerciseToEdit: Exercise,
    breakToEdit: BreakAfterExercise?,
    onAddOrEditButtonClick: () -> Unit,
    trainingType: TrainingType,
    isAutoBreakClicked: Boolean
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs =
        listOf(stringResource(id = R.string.exercise), stringResource(id = R.string.exercise_break))

    var currentExercise by remember { mutableStateOf(Exercise()) }
    currentExercise = exerciseToEdit.copy()
    var currentBreak: BreakAfterExercise? by remember { mutableStateOf(BreakAfterExercise()) }
    if (breakToEdit != null) {
        currentBreak = breakToEdit.copy()
    } else if (isAutoBreakClicked) {
        currentBreak!!.duration = viewModel.getAutoBreakDuration()
    }

    var isTimelessExercise: Boolean by remember {
        mutableStateOf(true)
    }

    val doesExerciseExists = currentExercise.name != ""
    val doesBreakExists = breakToEdit != null

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
                    0 -> ExerciseTab(
                        editedExercise = currentExercise,
                        onNameChange = {
                            currentExercise.name = it
                        },
                        onDurationChange = { currentExercise.duration = it },
                        isTimelessExercise = isTimelessExercise,
                        onCheckboxChange = {
                            isTimelessExercise = !isTimelessExercise
                        }
                    )
                    1 -> BreakTab(
                        editedBreak = currentBreak,
                        onDurationChange = {
                            if (currentBreak == null) {
                                currentBreak = BreakAfterExercise()
                            }
                            currentBreak?.duration = it
                        },
                    )
                }

                AddOrEditExerciseButton(
                    onClick = {
                        if (currentExercise.name.isNotEmpty() && currentExercise.duration != 0) {
                            onAddOrEditButtonClick()
                            if (doesExerciseExists) {
                                if (isExerciseChanged(currentExercise, exerciseToEdit)) {
                                    viewModel.editActivity(
                                        Activity(
                                            currentExercise.name,
                                            currentExercise.activityOrder,
                                            currentExercise.duration,
                                            getActivityType(
                                                isTimelessExercise = isTimelessExercise,
                                                trainingType = trainingType
                                            )
                                        )
                                    )
                                    Toast.makeText(
                                        context,
                                        R.string.exercise_edited,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                                if (!doesBreakExists && isBreakInitialized(currentBreak)) {
                                    viewModel.addBreakAfterActivity(
                                        Activity(
                                            "",
                                            currentExercise.activityOrder!!.plus(1),
                                            currentBreak!!.duration!!,
                                            ActivityType.BREAK
                                        )
                                    )
                                } else if (isBreakInitialized(currentBreak) && isBreakChanged(
                                        currentBreak,
                                        breakToEdit
                                    )
                                ) {
                                    viewModel.editActivity(
                                        Activity(
                                            "",
                                            currentExercise.activityOrder!!.plus(1),
                                            currentBreak!!.duration!!,
                                            ActivityType.BREAK
                                        )
                                    )
                                }else if(isBreakChanged(currentBreak,breakToEdit) && currentBreak!!.duration!! == 0 ){
                                    viewModel.removeLocalActivity(currentExercise.activityOrder!!.plus(1))
                                }
                            } else {
                                if (isBreakInitialized(currentBreak)) {
                                    viewModel.addActivityWithBreak(
                                        currentExercise.toActivity(
                                            trainingType,
                                            isTimelessExercise
                                        ),
                                        Activity(
                                            "",
                                            null,
                                            currentBreak!!.duration!!,
                                            ActivityType.BREAK
                                        )
                                    )
                                } else {
                                    viewModel.addActivity(
                                        currentExercise.toActivity(trainingType, isTimelessExercise)
                                    )
                                }
                                Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_LONG)
                                    .show()
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
                    isExerciseOrBreakChanged = isExerciseChanged(
                        currentExercise,
                        exerciseToEdit
                    ) || isBreakChanged(currentBreak, breakToEdit)
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
    val sliderMinValue = 10
    val sliderMaxValue = 300
    var exerciseDuration: Int by remember { mutableStateOf(sliderMinValue) }
    var exerciseName: String by remember { mutableStateOf("") }

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
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        text = stringResource(R.string.duration) + " ${
            toDisplayableLength(
                exerciseDuration
            )
        }",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    if (isTimelessExercise) {
        TimelessExerciseCheckbox(onClick = { onCheckboxChange(it) }, isTimelessExercise = true)
    } else {
        TimelessExerciseCheckbox(onClick = { onCheckboxChange(it) }, isTimelessExercise = false)
        Slider(
            value = exerciseDuration.toFloat(),
            onValueChange = {
                exerciseDuration = it.toInt()
                onDurationChange(exerciseDuration)
            },
            valueRange = 10f..sliderMaxValue.toFloat(),
            modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
        )
    }
    AddOrSubtractButtons { changeValue ->
        if (exerciseDuration + changeValue in 10..300) {
            exerciseDuration += changeValue
            onDurationChange(exerciseDuration)
        }
    }
}

@Composable
private fun BreakTab(
    editedBreak: BreakAfterExercise?,
    onDurationChange: (duration: Int) -> Unit,
) {
    val sliderMinValue = 0
    val sliderMaxValue = 300
    var breakDuration: Int by remember { mutableStateOf(sliderMinValue) }
    LaunchedEffect(editedBreak) {
        if (editedBreak?.duration != null) {
            breakDuration = editedBreak.duration!!
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

private fun isBreakChanged(currentBreak: BreakAfterExercise?, breakToEdit: BreakAfterExercise?) =
    currentBreak?.duration != breakToEdit?.duration

private fun isBreakInitialized(currentBreak: BreakAfterExercise?) =
    currentBreak?.duration != 0 && currentBreak?.duration != null

private fun getActivityType(isTimelessExercise: Boolean, trainingType: TrainingType) =
    if (trainingType == TrainingType.STRETCH) ActivityType.STRETCH
    else if (isTimelessExercise) ActivityType.TIMELESS_EXERCISE
    else ActivityType.EXERCISE

private fun Exercise.toActivity(trainingType: TrainingType, isTimelessExercise: Boolean): Activity =
    Activity(
        this.name,
        this.activityOrder,
        this.duration,
        getActivityType(isTimelessExercise = isTimelessExercise, trainingType)
    )


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
fun TimelessExerciseCheckbox(onClick: (isChecked: Boolean) -> Unit, isTimelessExercise: Boolean) {
    var isChecked by remember { mutableStateOf(isTimelessExercise) }

    Row(
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
