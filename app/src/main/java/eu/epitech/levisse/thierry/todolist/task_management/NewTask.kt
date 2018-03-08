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


class NewTask : AppCompatActivity(), TaskManager {

    lateinit var mDbWorkerThread: DbWorkerThread

    var imageUriString: String = ""

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var mTimeSetListener: TimePickerDialog.OnTimeSetListener

    lateinit var mDatePickerDialog: DatePickerDialog
    lateinit var mTimePickerDialog: TimePickerDialog

    lateinit var cal: Calendar
    lateinit var text_date: TextView
    lateinit var text_time: TextView


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

    override fun dateDialog() {

        text_date = findViewById(R.id.text_date)

//        cal.add(Calendar.DAY_OF_YEAR, 1)
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        updateDateText(year, month, day)

        mDateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            Log.d(TAG, "onDateSet: dd/mm/yyy: $day/$month$/$year")

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
            Log.d(TAG, "onTimeSet: hh:mm: $hour:$min")

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

    fun galleryButton() {
        val gallery_button: Button = findViewById(R.id.pick_gallery)

        gallery_button.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_READ_GALLERY)

            } else {
                val pickPhoto = Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 0)

            }
        }

    }

    fun cameraButton() {
        val camera_button: Button = findViewById(R.id.pick_camera)

        camera_button.setOnClickListener {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSION_CAMERA)

            } else {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(takePicture, 1)
                Toast.makeText(this@NewTask, "Camera disabled", Toast.LENGTH_LONG).show()

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        if (imageReturnedIntent == null)
            return

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        val imageview: ImageView = findViewById(R.id.image_task_selection)

        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                val selectedImage = imageReturnedIntent.data
                imageview.setImageURI(selectedImage)
                imageUriString = imageReturnedIntent.dataString
            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                val extras = imageReturnedIntent.extras
                val imageBitmap = extras.get("data") as Bitmap

                imageview.setImageBitmap(imageBitmap)

                val tempUri = getImageUri(applicationContext, imageBitmap)
                imageUriString = tempUri.toString()

                Toast.makeText(this@NewTask, "Here " + getRealPathFromURI(tempUri), Toast.LENGTH_LONG).show()
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CAMERA -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(takePicture, 1)
                    Toast.makeText(this@NewTask, "Camera disabled", Toast.LENGTH_LONG).show()


                } else {

                    Toast.makeText(this@NewTask, "Camera permission needed", Toast.LENGTH_LONG).show()

                }
                return
            }

            PERMISSION_READ_GALLERY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    val pickPhoto = Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 0)
                } else {

                    Toast.makeText(this@NewTask, "External read permission needed", Toast.LENGTH_LONG).show()

                }
                return
            }

        }
    }
}
