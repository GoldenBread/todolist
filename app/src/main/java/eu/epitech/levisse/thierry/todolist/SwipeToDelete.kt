package eu.epitech.levisse.thierry.todolist

/**
 * Created by thierry on 04/02/18.
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log

abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp)
    private val intrinsicWidth = icon.intrinsicWidth
    private val intrinsicHeight = icon.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColorDelete = Color.parseColor("#f44336")
    private val backgroundColorDone = Color.parseColor("#64DD17")
    private val context = context

    companion object {
        private val TAG = SwipeToDeleteCallback::class.qualifiedName

    }


    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        Log.d(TAG,"${viewHolder.adapterPosition}")
        if (viewHolder.adapterPosition >= 0 &&
                TaskList.getInstance()?.size ?: 0 > viewHolder.adapterPosition &&
                TaskList.getInstance()?.get(viewHolder.adapterPosition)?.frequence ?: 0 == -1)
            return

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        // Draw the red delete background
        if (viewHolder.adapterPosition >= 0 &&
                TaskList.getInstance()?.size ?: 0 > viewHolder.adapterPosition &&
                TaskList.getInstance()?.get(viewHolder.adapterPosition)?.done ?: 1 == 0) {
            background.color = backgroundColorDone
            icon = ContextCompat.getDrawable(context, R.drawable.ic_done_white_24dp)
        } else {
            background.color = backgroundColorDelete
            icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp)
        }

        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        // Calculate position of delete icon
        val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val iconMargin = (itemHeight - intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + intrinsicHeight

        // Draw the delete icon
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

    }
}