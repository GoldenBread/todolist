package eu.epitech.levisse.thierry.todolist

/**
 * Created by thierry on 04/02/18.
 */

class TaskList {
    companion object {
        private var INSTANCE: MutableList<Task>? = null

        fun getInstance(): MutableList<Task>? {
            if (INSTANCE == null) {
                INSTANCE = mutableListOf()
            }
            return INSTANCE
        }

        fun setInstance(tasks: MutableList<Task>) {
            INSTANCE = tasks
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}