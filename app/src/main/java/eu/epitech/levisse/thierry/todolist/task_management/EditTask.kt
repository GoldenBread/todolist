package eu.epitech.levisse.thierry.todolist.task_management

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import eu.epitech.levisse.thierry.todolist.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by thierry on 03/02/18.
 */

class EditTask : ATaskManager() {

    lateinit var task: Task


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_management)

        val txt_header: TextView = findViewById(R.id.task_management_header)

        txt_header.text = getString(R.string.header_edit_task)

        task = intent.getParcelableExtra("task")


        mDbWorkerThread = DbWorkerThread(UUID.randomUUID().toString())
        mDbWorkerThread.start()

        //Image picker
        imageUriString = task.imageLocation
        val image_selected: ImageView = findViewById(R.id.image_task_selection)
        image_selected.setImageURI(Uri.parse(imageUriString))

        galleryButton()

        cameraButton()


        //Deadline date
        cal = Calendar.getInstance()
        switchDatetime()
        dateDialog()
        timeDialog()

        //Frequency radio
        val radio_onetime: RadioButton = findViewById(R.id.radioButton_one_time)
        val radio_daily: RadioButton = findViewById(R.id.radioButton_daily)

        if (task.frequence == 0)
            radio_onetime.isChecked = true
        else
            radio_daily.isChecked = true

        saveButton()
    }

    override fun switchDatetime() {
        val switch: Switch = findViewById(R.id.switch_datetime)

        val datetime: ConstraintLayout = findViewById(R.id.datetime_layout)
        if (task.deadlineDate.after(Date(10))) {
            switch.isSelected = true
            Log.d("switch", "${task.deadlineDate.hours}:${task.deadlineDate.minutes}")
            cal.time = task.deadlineDate
        }
        else
            datetime.visibility = View.GONE


        switch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                datetime.visibility = View.VISIBLE
                cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, 1)

            }
            else {
                datetime.visibility = View.GONE
                cal.timeInMillis = 1

            }
        }

    }

    override fun saveButton() {

        val button: Button = findViewById(R.id.button_save)

        val title_txt: EditText = findViewById(R.id.title_input_text)
        val description_txt: EditText = findViewById(R.id.description_input_text)

        title_txt.setText(task.title)
        description_txt.setText(task.description)

        button.setOnClickListener { view ->
            val task = Runnable {
//                val email: EditText = findViewById(R.id.email)

                Log.d("EditTask", "Title: ${title_txt.text}, Description: ${description_txt.text}")
                Log.d("EditTask", "Date selected dd/mm/yyy hh:mm: " +
                        "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH)}/${cal.get(Calendar.YEAR)} " +
                        "${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)}")

                AppDatabase.getInstance(this)?.taskDao()?.updateTask(
                        Task(
                                title_txt.text.toString(),
                                description_txt.text.toString(),
                                Calendar.getInstance().time,
                                cal.time,
                                imageUriString,
                                getFrequency(),
                                0,
                                task.id
                        )
                )
            }

            mDbWorkerThread.postTask(task)

            setResult(RESULT_OK, null)
            finish()
        }
    }
}
