package com.example.stretchy.features.createtraining.ui.composable

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
import com.example.stretchy.features.createtraining.ui.composable.buttons.CreateOrEditTrainingButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.TrainingName
import com.example.stretchy.features.createtraining.ui.composable.list.ExerciseListAdapter
import com.example.stretchy.features.createtraining.ui.composable.list.ExercisesWithBreaks
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity

@Composable
fun CreateTrainingComposable(
    navController: NavController,
    viewModel: CreateOrEditTrainingViewModel
) {
    var trainingName: String by remember { mutableStateOf("") }
    var trainingId: Long? by remember { mutableStateOf(null) }
    var isTrainingBeingEdited by remember { mutableStateOf(false) }
    var isListInitialized by remember { mutableStateOf(false) }
    var isAutoBreakButtonClicked by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var exerciseList: List<ExercisesWithBreaks> by remember {
        mutableStateOf(mutableListOf())
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val newList = exerciseList.toMutableList()
                newList.add(
                    ExercisesWithBreaks(
                        exerciseList.lastIndex + 1,
                        Exercise(),
                        if (isAutoBreakButtonClicked) viewModel.getAutoBreakDuration() else 0,
                        isExpanded = true
                    )
                )
                exerciseList = newList
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.desc_plus_icon)
                )
            }
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(top = 16.dp, bottom = 64.dp)
            ) {
                when (val state = viewModel.uiState.collectAsState().value) {
                    is CreateTrainingUiState.Success -> {
                        trainingId = state.trainingId
                        trainingName = state.currentName
                        TrainingName(viewModel, trainingName)
                        AutoBreakCheckbox(viewModel = viewModel)
                        isTrainingBeingEdited = state.editingTraining
                        isAutoBreakButtonClicked = state.isAutomaticBreakButtonClicked
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!isListInitialized) {
                            exerciseList = state.activities.toExerciseWithBreaks()
                            isListInitialized = true
                        }
                        RecyclerViewContainer(
                            activitiesWithoutBreaks = exerciseList,
                            onListChange = { exerciseList = it }
                        )
                    }
                    is CreateTrainingUiState.Error -> {
                        HandleError(state = state, context = context)
                    }
                    is CreateTrainingUiState.Done -> {
                        if (navController.currentDestination?.route != Screen.StretchingListScreen.route) {
                            navController.navigate(Screen.StretchingListScreen.route)
                        }
                    }
                    CreateTrainingUiState.Init -> {}
                }
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                CreateOrEditTrainingButton(
                    viewModel,
                    isTrainingBeingEdited,
                    trainingId,
                    exerciseList
                )
            }
        }
    }
}

@Composable
fun RecyclerViewContainer(
    activitiesWithoutBreaks: List<ExercisesWithBreaks>,
    onListChange: (exerciseList: List<ExercisesWithBreaks>) -> Unit
) {
    var recyclerView: RecyclerView? = null
    val adapter: ExerciseListAdapter by remember {
        mutableStateOf(
            ExerciseListAdapter(
                activitiesWithoutBreaks,
                scrollToPosition = { recyclerView?.scrollToPosition(it) },
                onListChange
            )
        )
    }
    adapter.submitList(activitiesWithoutBreaks)
    adapter.notifyDataSetChanged()

    val dragAndReorderItemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : SimpleCallback(
                UP or
                        DOWN, 0
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }
    val deleteItemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : SimpleCallback(
                0,
                LEFT or RIGHT
            ) {
                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    holder: RecyclerView.ViewHolder
                ): Int {
                    val position = holder.adapterPosition
                    Log.e("asditemcount ", adapter.itemCount.toString())
                    Log.e("asdpos", position.toString())
                    return createSwipeFlags(position, recyclerView, holder)
                }

                private fun createSwipeFlags(
                    position: Int,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                 //   adapter
                //    Log.e("asd", activitiesWithoutBreaks[position].toString())
                    return if (adapter.isItemExpanded(position)) 0 else super.getSwipeDirs(
                        recyclerView,
                        viewHolder
                    )
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.adapterPosition

                    adapter.removeItem(position)
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    AndroidView(
        factory = { context ->
            recyclerView = RecyclerView(context)
            recyclerView!!.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager = LinearLayoutManager(context)
            dragAndReorderItemTouchHelper.attachToRecyclerView(recyclerView)
            deleteItemTouchHelper.attachToRecyclerView(recyclerView)
            recyclerView!!

        }, update = { recyclerView ->
            recyclerView.adapter = adapter
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
private fun HandleError(state: CreateTrainingUiState.Error, context: Context) {
    when (state.reason) {
        CreateTrainingUiState.Error.Reason.MissingTrainingName -> {
            Toast.makeText(
                context,
                R.string.specify_training_name,
                Toast.LENGTH_LONG
            ).show()
        }
        CreateTrainingUiState.Error.Reason.NotEnoughExercises -> {
            Toast.makeText(
                context,
                R.string.add_min_2_exercises,
                Toast.LENGTH_LONG
            ).show()
        }
        is CreateTrainingUiState.Error.Reason.Unknown -> {
            Toast.makeText(
                context,
                R.string.something_went_wrong,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

@Composable
private fun AutoBreakCheckbox(viewModel: CreateOrEditTrainingViewModel) {
    var isAutoBreakChecked by remember { mutableStateOf(true) }
    var autoBreakDuration by remember { mutableStateOf("${viewModel.getAutoBreakDuration()}") }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isAutoBreakChecked,
            onCheckedChange = {
                isAutoBreakChecked = it
                if (isAutoBreakChecked) {
                    viewModel.enableAutoBreaks()
                } else {
                    viewModel.disableAutoBreaks()
                }
            },
        )
        Text(
            modifier = Modifier,
            maxLines = 1,
            text = "Append automatic break: ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        BasicTextField(
            modifier = Modifier
                .padding(start = 4.dp)
                .width(32.dp),
            value = autoBreakDuration,
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true,
            onValueChange = {
                if (it.length <= 3 && it.isDigitsOnly()) {
                    autoBreakDuration = it
                    if (it.isNotBlank()) {
                        viewModel.updateAutoBreakDuration(it.toInt())
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(
            modifier = Modifier,
            maxLines = 1,
            text = "(secs)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun List<Activity>.toExerciseWithBreaks(): List<ExercisesWithBreaks> {
    val list = mutableListOf<ExercisesWithBreaks>()
    // var listId = 0
    this.forEachIndexed() { index, item ->
        if (item.activityType != ActivityType.BREAK) {
            list.add(
                ExercisesWithBreaks(
                    list.lastIndex + 1,
                    Exercise(
                        item.activityId.toInt(),
                        item.name,
                        item.activityOrder,
                        item.duration
                    ),
                    if (this.getOrNull(index + 1)?.activityType == ActivityType.BREAK) this.getOrNull(
                        index + 1
                    )?.duration
                    else null, false
                )
            )
        }
    }
    return list
}