package com.example.stretchy.features.createtraining.ui.composable.list

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@Composable
fun RecyclerView(
    activitiesWithoutBreaks: List<ExercisesWithBreaks>,
    onListChange: (exerciseList: List<ExercisesWithBreaks>) -> Unit
) {
    var recyclerView: RecyclerView? by remember { mutableStateOf(null) }
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

    val dragAndReorderItemTouchHelper by lazy {
        val simpleItemTouchCallback = getDragAndReorderSimpleItemCallback(adapter)
        ItemTouchHelper(simpleItemTouchCallback)
    }
    val deleteItemTouchHelper by lazy {
        val simpleItemTouchCallback = getSwipeToDeleteSimpleItemCallback(adapter)
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

private fun getDragAndReorderSimpleItemCallback(adapter: ExerciseListAdapter) =
    object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or
                ItemTouchHelper.DOWN, 0
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

private fun getSwipeToDeleteSimpleItemCallback(adapter: ExerciseListAdapter) =
    object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            holder: RecyclerView.ViewHolder
        ): Int {
            val position = holder.adapterPosition
            return createSwipeFlags(position, recyclerView, holder)
        }

        private fun createSwipeFlags(
            position: Int,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
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