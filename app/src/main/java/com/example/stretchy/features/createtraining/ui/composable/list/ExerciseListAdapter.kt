package com.example.stretchy.features.createtraining.ui.composable.list

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.stretchy.features.createtraining.ui.composable.list.listitem.ExerciseListItem
import com.example.stretchy.features.createtraining.ui.composable.widget.OnListExerciseHandler
import java.util.*

class ExerciseListAdapter(
    private var exercisesWithBreaks: List<ExercisesWithBreaks>,
    private val scrollToPosition: (position: Int) -> Unit,
    private val onListChange: (exerciseList: List<ExercisesWithBreaks>) -> Unit
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
                item,
                position = position,
                onExpand = {
                    getCurrentExpandedItemPosition()?.let {
                        hideExpandedItem(it)
                    }
                    removeBlankItem()
                    expandItem(position)
                },
                onCollapse = {
                    hideExpandedItem(position)
                },
                isExpanded = item.isExpanded,
                onListExerciseHandler = object : OnListExerciseHandler {
                    override fun addExercise(exercise: ExercisesWithBreaks) {
                        addNewExercise(exercise = exercise)
                    }

                    override fun editExercise(exercise: ExercisesWithBreaks) {
                        editCurrentExercise(exercise = exercise)
                    }

                    override fun removeBreak(position: Int) {
                        exercisesWithBreaks[position].nextBreakDuration = null
                        onListChange(exercisesWithBreaks)
                    }

                }
            )
        }
    }

    inner class ViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

    fun moveItem(from: Int, to: Int) {
        Collections.swap(exercisesWithBreaks, from, to)
        notifyItemMoved(from, to)
        onListChange(exercisesWithBreaks)
    }

    fun submitList(newList: List<ExercisesWithBreaks>) {
        exercisesWithBreaks = newList
        notifyDataSetChanged()
        getCurrentExpandedItemPosition()?.let {
            if (exercisesWithBreaks.lastIndex != it) {
                hideExpandedItem(it)
            }
        }
        scrollToPosition(exercisesWithBreaks.lastIndex)
    }

    fun addNewExercise(exercise: ExercisesWithBreaks) {
        val newList = exercisesWithBreaks.toMutableList()
        newList.add(exercise)
        exercisesWithBreaks = newList
        onListChange(exercisesWithBreaks)
    }

    fun isItemExpanded(position: Int) = exercisesWithBreaks[position].isExpanded

    fun removeItem(position: Int) {
        val newList = exercisesWithBreaks.toMutableList()
        newList.removeAt(position)
        exercisesWithBreaks = newList
        onListChange(exercisesWithBreaks)
        notifyItemRemoved(position)
    }

    fun removeBlankItem() {
        if (exercisesWithBreaks[exercisesWithBreaks.lastIndex].exercise.name == "") {
            removeItem(exercisesWithBreaks.lastIndex)
        }
    }

    private fun getCurrentExpandedItemPosition(): Int? {
        val position = exercisesWithBreaks.indexOf(exercisesWithBreaks.find { it.isExpanded })
        return if (position != RecyclerView.NO_POSITION) position else null
    }

    private fun expandItem(position: Int) {
        exercisesWithBreaks[position].isExpanded = true
        notifyItemChanged(position)
        scrollToPosition(position)
    }

    private fun hideExpandedItem(position: Int) {
        exercisesWithBreaks[position].isExpanded = false
        notifyItemChanged(position)
    }

    private fun editCurrentExercise(exercise: ExercisesWithBreaks) {
        val newList = exercisesWithBreaks.toMutableList()
        newList[exercise.listId] = exercise
        exercisesWithBreaks = newList
        onListChange(exercisesWithBreaks)
    }
}
