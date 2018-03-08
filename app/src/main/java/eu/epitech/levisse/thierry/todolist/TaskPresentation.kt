package eu.epitech.levisse.thierry.todolist

/**
 * Created by thierry on 30/01/18.
 */

interface TaskPresentation {

    fun showTasks(tasks: List<Task>)

    fun taskAddedAt(position: Int)

    fun scrollTo(position: Int)
}