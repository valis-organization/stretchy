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

class ExerciseListAdapter(
    private val viewModel: CreateOrEditTrainingViewModel,
    private val trainingType: TrainingType,
    private val isAutoBreakClicked: Boolean,
    private val onEditClick: () -> Unit
) :
    ListAdapter<ExercisesWithBreaks, ExerciseListAdapter.ViewHolder>(ItemDiffCallback()) {

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
                    id = item.exercise.id,
                    name = item.exercise.name,
                    activityOrder = null,
                    duration = item.exercise.duration,
                ),
                BreakAfterExercise(item.nextBreakDuration),
                position = position,
                trainingType = trainingType,
                isAutoBreakClicked = isAutoBreakClicked,
                onEditClick = onEditClick,
                onExpand = {
                    val mutableList = currentList.toMutableList()

                    getCurrentExpandedItemPosition()?.let {
                        mutableList[it] = getItem(it).copy(isExpanded = false)
                    }
                    mutableList[position] =
                        getItem(position).copy(isExpanded = true)
                    submitList(mutableList)
                },
                onCollapse = {
                    val mutableList = currentList.toMutableList()
                    mutableList[position] =
                        getItem(position).copy(isExpanded = false)
                    submitList(mutableList)
                },
                isExpanded = item.isExpanded
            )
        }
    }

    inner class ViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    private class ItemDiffCallback : DiffUtil.ItemCallback<ExercisesWithBreaks>() {
        override fun areItemsTheSame(
            oldItem: ExercisesWithBreaks,
            newItem: ExercisesWithBreaks
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ExercisesWithBreaks,
            newItem: ExercisesWithBreaks
        ): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
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

    private fun getCurrentExpandedItemPosition(): Int? {
        val position = currentList.indexOf(currentList.find { it.isExpanded })
        return if (position != RecyclerView.NO_POSITION) position else null
    }
}