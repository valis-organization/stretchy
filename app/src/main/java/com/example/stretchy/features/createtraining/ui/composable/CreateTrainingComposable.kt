package com.example.stretchy.features.createtraining.ui.composable

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.database.data.ActivityType
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.CreateTrainingUiState
import com.example.stretchy.features.createtraining.ui.composable.list.ExerciseListAdapter
import com.example.stretchy.features.createtraining.ui.composable.buttons.CreateOrEditTrainingButton
import com.example.stretchy.features.createtraining.ui.composable.buttons.TrainingName
import com.example.stretchy.repository.Activity

@Composable
fun CreateTrainingComposable(
    navController: NavController,
    viewModel: CreateOrEditTrainingViewModel
) {
    var trainingName: String by remember { mutableStateOf("") }
    var trainingId: Long? by remember { mutableStateOf(null) }
    var isTrainingBeingEdited by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            Modifier
                .padding(top = 16.dp)
        ) {
            when (val state = viewModel.uiState.collectAsState().value) {
                is CreateTrainingUiState.Success -> {
                    trainingId = state.trainingId
                    trainingName = state.currentName
                    TrainingName(viewModel, trainingName)
                    AutoBreakCheckbox(viewModel = viewModel)
                    isTrainingBeingEdited = state.editingTraining
                    Spacer(modifier = Modifier.height(4.dp))
                    RecyclerViewContainer(
                        activitiesWithoutBreaks = getExercisesWithoutBreaks(state.activities),
                        viewModel = viewModel,
                        state.trainingType,
                        state.isAutomaticBreakButtonClicked
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
        Spacer(modifier = Modifier.height(200.dp))
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            CreateOrEditTrainingButton(viewModel, isTrainingBeingEdited, navController, trainingId)
        }
    }
}


@Composable
fun RecyclerViewContainer(
    activitiesWithoutBreaks: MutableList<Activity>, viewModel: CreateOrEditTrainingViewModel,
    trainingType: TrainingType,
    isAutoBreakClicked: Boolean
) {
    Log.e("asd", activitiesWithoutBreaks.toString())
    val adapter =
        ExerciseListAdapter(viewModel, trainingType, isAutoBreakClicked) {}
    adapter.submitList(activitiesWithoutBreaks)
    adapter.notifyDataSetChanged()
    val itemTouchHelper by lazy {
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

                    val adapter = recyclerView.adapter as ExerciseListAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)

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
    val asd by lazy {
        val simpleItemTouchCallback =
            object : SimpleCallback(
                0,
                LEFT or RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.adapterPosition
                    viewModel.removeLocalActivityByListPosition(position)
                    adapter.notifyDataSetChanged()
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    AndroidView(
        factory = { context ->
            val recyclerView = RecyclerView(context)
            recyclerView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            asd.attachToRecyclerView(recyclerView)
            recyclerView

        }, update = { recyclerView ->
            // You can update the RecyclerView here if needed
            recyclerView.adapter = adapter
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .weight(1f),
            text = "auto breaks",
            fontSize = 16.sp,

            fontWeight = FontWeight.Bold
        )
    }
}

private fun getExercisesWithoutBreaks(activities: List<Activity>): MutableList<Activity> {
    val new = mutableListOf<Activity>()
    activities.forEach {
        if (it.activityType != ActivityType.BREAK) {
            new.add(it)
        }
    }
    return new
}