package com.example.stretchy.features.createtraining.ui.composable.list

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.BreakAfterExercise
import com.example.stretchy.features.createtraining.ui.data.Exercise
import com.example.stretchy.repository.Activity

class ExerciseListAdapter(
    private val viewModel: CreateOrEditTrainingViewModel,
    private val trainingType: TrainingType,
    private val isAutoBreakClicked: Boolean,
    private val onEditClick: () -> Unit
) :
    ListAdapter<Activity, ExerciseListAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val composeView = ComposeView(context)

        return ViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position)
        holder.composeView.setContent {
            ExerciseListItem(
                vm = viewModel,
                exercise = Exercise(
                    name = item.name,
                    activityOrder = item.activityOrder,
                    duration = item.duration,
                    position
                ),
                BreakAfterExercise(5),
                trainingType = trainingType,
                isAutoBreakClicked = isAutoBreakClicked,
                onEditClick = onEditClick
            )
        }
    }

    inner class ViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    private class ItemDiffCallback : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.activityOrder == newItem.activityOrder
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem == newItem
        }
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val fromItem = getItem(fromPosition)
        val toItem = getItem(toPosition)
        val tempList = currentList.toMutableList()
        tempList[fromPosition] = toItem
        tempList[toPosition] = fromItem
        submitList(tempList)
    }

    fun moveItem(from: Int, to: Int) {
        val item = getItem(from)
        val tempList = currentList.toMutableList()
        tempList.removeAt(from)
        tempList.add(to, item)
        submitList(tempList)
    }
}