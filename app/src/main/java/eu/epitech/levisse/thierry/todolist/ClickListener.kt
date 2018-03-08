package eu.epitech.levisse.thierry.todolist

import android.view.View

/**
 * Created by thierry on 03/02/18.
 */

interface ClickListener {
    fun OnClick(view: View, position: Int)
    fun OnLongClick(view: View, position: Int)
}