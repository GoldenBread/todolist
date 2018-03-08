package eu.epitech.levisse.thierry.todolist

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

/**
 * Created by thierry on 29/01/18.
 */

@Dao interface TaskDao {

    @Query("SELECT * FROM task ORDER BY done ASC, deadlineDate ASC")
    fun getAllTasks(): MutableList<Task>

    @Insert
    fun insertAll(task: Task)

    @Update
    fun updateTask(task: Task)

    @Query("DELETE FROM task")
    fun deleteTasks()

    @Query("DELETE FROM task WHERE id = :arg0")
    fun deleteTasks(queryWhere: String)
}