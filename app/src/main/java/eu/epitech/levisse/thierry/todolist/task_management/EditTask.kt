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

class EditTask : AppCompatActivity(), TaskManager {

    lateinit var mDbWorkerThread: DbWorkerThread

    lateinit var imageUriString: String

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var mTimeSetListener: TimePickerDialog.OnTimeSetListener

    lateinit var mDatePickerDialog: DatePickerDialog
    lateinit var mTimePickerDialog: TimePickerDialog

    lateinit var cal: Calendar
    lateinit var text_date: TextView
    lateinit var text_time: TextView

    lateinit var task: Task


    companion object {
        private val TAG = EditTask::class.qualifiedName

    }

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

        val gallery_button: Button = findViewById(R.id.pick_gallery)

        gallery_button.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, 0)


        }

        val camera_button: Button = findViewById(R.id.pick_camera)

        camera_button.setOnClickListener {
            val permission = android.Manifest.permission.CAMERA
            if (this.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 1)

            } else {
                Toast.makeText(this@EditTask, "Camera permission needed", Toast.LENGTH_LONG).show()

            }
        }

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

    override fun dateDialog() {

        text_date = findViewById(R.id.text_date)

        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        updateDateText(year, month, day)

        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            Log.d("date", "onDateSet: dd/mm/yyy: $day/$month$/$year")

            updateDateText(year, month, day)
        }

        mDatePickerDialog = DatePickerDialog(
                this,
                mDateSetListener,
                year, month, day
        )

        text_date.setOnClickListener {

            mDatePickerDialog.show()

        }

    }

    override fun updateDateText(year: Int, month: Int, day: Int) {
        val newDate = GregorianCalendar(year, month, day).time
        var spf = SimpleDateFormat("EEE dd MMM yyyy")

        text_date.text = spf.format(newDate)

        cal.set(year, month, day)
    }

    override fun timeDialog() {

        text_time = findViewById(R.id.text_time)

        val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
        val min: Int = cal.get(Calendar.MINUTE)
        updateTimeText(hour, min)

        mTimeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, min ->
            Log.d("NewTask", "onTimeSet: hh:mm: $hour:$min")

            updateTimeText(hour, min)
        }

        mTimePickerDialog = TimePickerDialog(
                this,
                mTimeSetListener,
                hour, min,
                true
        )

        text_time.setOnClickListener {
            mTimePickerDialog.show()
        }
    }

    override fun updateTimeText(hour: Int, min: Int) {
        text_time.text = String.format("%02d:%02d", hour, min)

        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, min)
    }

    override fun getFrequency(): Int {
        val radio_onetime: RadioButton = findViewById(R.id.radioButton_one_time)

        if (radio_onetime.isChecked)
            return 0
        return 1
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        val imageview: ImageView = findViewById(R.id.image_task_selection)

        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK && imageReturnedIntent.data != null) {
                val selectedImage = imageReturnedIntent.data
                imageview.setImageURI(selectedImage)
                imageUriString = imageReturnedIntent.dataString
            }
            1 -> if (resultCode == Activity.RESULT_OK && imageReturnedIntent.data != null) {
                val extras = imageReturnedIntent.extras
                val imageBitmap = extras.get("data") as Bitmap

                imageview.setImageBitmap(imageBitmap)

                val tempUri = getImageUri(applicationContext, imageBitmap)
                imageUriString = tempUri.toString()

                Toast.makeText(this@EditTask, "Here " + getRealPathFromURI(tempUri), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }
}
