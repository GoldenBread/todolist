package eu.epitech.levisse.thierry.todolist.task_management

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import eu.epitech.levisse.thierry.todolist.DbWorkerThread
import java.time.Month
import java.util.*

/**
 * Created by thierry on 03/02/18.
 */


interface TaskManager {
    fun switchDatetime()

    fun galleryButton()
    fun cameraButton()

    fun dateDialog()
    fun updateDateText(year: Int, month: Int, day: Int)

    fun timeDialog()
    fun updateTimeText(hour: Int, min: Int)

    fun getFrequency(): Int

    fun saveButton()

}