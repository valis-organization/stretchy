package com.example.stretchy.features.createtraining.ui.composable.list

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.stretchy.database.data.TrainingType
import com.example.stretchy.features.createtraining.ui.CreateOrEditTrainingViewModel
import com.example.stretchy.features.createtraining.ui.data.BreakAfterExercise
import com.example.stretchy.features.createtraining.ui.data.Exercise
import java.util.*

class ExerciseListAdapter(
    private var exercisesWithBreaks: List<ExercisesWithBreaks>,
    private val viewModel: CreateOrEditTrainingViewModel,
    private val trainingType: TrainingType,
    private val isAutoBreakClicked: Boolean,
    private val onEditClick: () -> Unit
) :
    RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val composeView = ComposeView(context)

        return ViewHolder(composeView)
    }

    override fun getItemCount(): Int {
        return exercisesWithBreaks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = exercisesWithBreaks[position]
        holder.composeView.setContent {
            ExerciseListItem(
                vm = viewModel,
                exercise = Exercise(
                    id = item.exercise.id,
                    name = item.exercise.name,
                    activityOrder = item.exercise.activityOrder,
                    duration = item.exercise.duration,
                ),
                BreakAfterExercise(item.nextBreakDuration),
                position = position,
                trainingType = trainingType,
                isAutoBreakClicked = isAutoBreakClicked,
                onEditClick = onEditClick,
                onExpand = {
                    getCurrentExpandedItemPosition()?.let {
                        exercisesWithBreaks[it].isExpanded = false
                        notifyItemChanged(it)
                    }
                    exercisesWithBreaks[position].isExpanded = true
                    notifyItemChanged(position)
                },
                onCollapse = {
                    exercisesWithBreaks[position].isExpanded = false
                    notifyItemChanged(position)
                },
                isExpanded = item.isExpanded
            )
        }
    }

    inner class ViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    fun moveItem(from: Int, to: Int) {
        Collections.swap(exercisesWithBreaks, from, to)
        notifyItemMoved(from, to)
    }

    private fun getCurrentExpandedItemPosition(): Int? {
        val position = exercisesWithBreaks.indexOf(exercisesWithBreaks.find { it.isExpanded })
        return if (position != RecyclerView.NO_POSITION) position else null
    }

    fun submitList(newList: List<ExercisesWithBreaks>) {
        exercisesWithBreaks = newList
        notifyDataSetChanged()
    }
}