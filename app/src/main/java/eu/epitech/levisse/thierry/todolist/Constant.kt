@file:JvmName("Constant")
package eu.epitech.levisse.thierry.todolist

/**
 * Created by thierry on 02/02/18.
 */


class Constant {
    class DB{
        companion object {
            @JvmField val DbName = "TasksDB"

            @JvmField val PERMISSION_CAMERA = 1
            @JvmField val PERMISSION_READ_GALLERY = 2

        }

    }
}