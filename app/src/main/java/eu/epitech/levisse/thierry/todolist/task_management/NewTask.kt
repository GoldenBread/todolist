package eu.epitech.levisse.thierry.todolist.task_management

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import android.provider.MediaStore
import android.widget.Toast
import android.graphics.Bitmap
import android.R.attr.data
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.NotificationCompat.getExtras
import android.provider.MediaStore.Images
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import eu.epitech.levisse.thierry.todolist.*
import eu.epitech.levisse.thierry.todolist.Constant.DB.Companion.PERMISSION_CAMERA
import eu.epitech.levisse.thierry.todolist.Constant.DB.Companion.PERMISSION_READ_GALLERY
import java.io.ByteArrayOutputStream
import java.security.AccessController.getContext


class NewTask : ATaskManager() {

    companion object {
        private val TAG = NewTask::class.qualifiedName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_management)


        mDbWorkerThread = DbWorkerThread(UUID.randomUUID().toString())
        mDbWorkerThread.start()

        //Image picker
        galleryButton()

        cameraButton()



        //Deadline date
        cal = Calendar.getInstance()
        switchDatetime()
        dateDialog()
        timeDialog()

        //Frequency radio
        val radio_onetime: RadioButton = findViewById(R.id.radioButton_one_time)
        radio_onetime.isChecked = true

        saveButton()
    }

    override fun switchDatetime() {

        val switch: Switch = findViewById(R.id.switch_datetime)

        val datetime: ConstraintLayout = findViewById(R.id.datetime_layout)
        datetime.visibility = View.GONE
        cal.timeInMillis = 1

        switch.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                datetime.visibility = View.VISIBLE
                cal = Calendar.getInstance()

                Log.d(TAG, "${cal.get(Calendar.YEAR)}")
                cal.add(Calendar.DAY_OF_YEAR, 1)

                updateDateText(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                updateTimeText(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

                mDatePickerDialog.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

                mTimePickerDialog.updateTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

            }
            else {
                datetime.visibility = View.GONE
                cal.timeInMillis = 1
                Log.d(TAG, "${cal.get(Calendar.YEAR)}")

                updateDateText(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                updateTimeText(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

            }
        }

    }



    override fun saveButton() {

        val button: Button = findViewById(R.id.button_save)

        button.setOnClickListener { view ->
            val task = Runnable {
                val title_txt: EditText = findViewById(R.id.title_input_text)
                val description_txt: EditText = findViewById(R.id.description_input_text)
//                val email: EditText = findViewById(R.id.email)

                Log.d(TAG, "Title: ${title_txt.text}, Description: ${description_txt.text}")
                Log.d(TAG, "Date selected dd/mm/yyy hh:mm: " +
                        "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH)}/${cal.get(Calendar.YEAR)} " +
                        "${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)}")


                AppDatabase.getInstance(this)?.taskDao()?.insertAll(
                        Task(
                                title_txt.text.toString(),
                                description_txt.text.toString(),
                                Calendar.getInstance().time,
                                cal.time,
                                imageUriString,
                                getFrequency()
                        )
                )
            }

            mDbWorkerThread.postTask(task)

            setResult(RESULT_OK, null)
            finish()
        }
    }


}
