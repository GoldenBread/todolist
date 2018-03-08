package eu.epitech.levisse.thierry.todolist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by thierry on 03/02/18.
 */

class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, clickListener: ClickListener): RecyclerView.OnItemTouchListener {

    lateinit var clickListener: ClickListener
    lateinit var gestureDetector: GestureDetector

    init {
        this.clickListener = clickListener
        gestureDetector = GestureDetector(context, object: GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                if (e == null)
                    return
                val child: View = recyclerView.findChildViewUnder(e!!.x, e!!.y)

                if (child != null && clickListener != null)
                    clickListener.OnLongClick(child, recyclerView.getChildAdapterPosition(child))
            }
        })
    }

    override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {

    }

    override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
        if (e == null)
            return false
        val child: View = rv?.findChildViewUnder(e.x, e.y) ?: return false

        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e) && rv != null)
            clickListener.OnClick(child, rv.getChildAdapterPosition(child))

        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

}