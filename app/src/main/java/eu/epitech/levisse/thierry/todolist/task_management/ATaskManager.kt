package eu.epitech.levisse.thierry.todolist.task_management

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import eu.epitech.levisse.thierry.todolist.Constant
import eu.epitech.levisse.thierry.todolist.Constant.DB.Companion.PERMISSION_CAMERA
import eu.epitech.levisse.thierry.todolist.Constant.DB.Companion.PERMISSION_READ_GALLERY
import eu.epitech.levisse.thierry.todolist.DbWorkerThread
import eu.epitech.levisse.thierry.todolist.R
import eu.epitech.levisse.thierry.todolist.Task
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by thierry on 08/03/18.
 */

abstract class ATaskManager: AppCompatActivity(), TaskManager {

    lateinit var mDbWorkerThread: DbWorkerThread

    lateinit var imageUriString: String

    lateinit var mDateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var mTimeSetListener: TimePickerDialog.OnTimeSetListener

    lateinit var mDatePickerDialog: DatePickerDialog
    lateinit var mTimePickerDialog: TimePickerDialog

    lateinit var cal: Calendar
    lateinit var text_date: TextView
    lateinit var text_time: TextView



    override fun galleryButton() {
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

    override fun cameraButton() {
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
                startActivityForResult(takePicture, 1)
//                Toast.makeText(this@NewTask, "Camera disabled", Toast.LENGTH_LONG).show()

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        if (resultCode != RESULT_CANCELED || imageReturnedIntent != null) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

            val imageview: ImageView = findViewById(R.id.image_task_selection)

            when (requestCode) {
                0 -> if (resultCode == Activity.RESULT_OK && imageReturnedIntent!!.data != null) {
                    val selectedImage = imageReturnedIntent.data
                    imageview.setImageURI(selectedImage)
                    imageUriString = imageReturnedIntent.dataString
                }
                1 -> if (resultCode == Activity.RESULT_OK && imageReturnedIntent!!.data != null) {
                    val extras = imageReturnedIntent.extras
                    val imageBitmap = extras.get("data") as Bitmap

                    imageview.setImageBitmap(imageBitmap)

                    val tempUri = getImageUri(applicationContext, imageBitmap)
                    imageUriString = tempUri.toString()

                    Toast.makeText(this@ATaskManager, "Here " + getRealPathFromURI(tempUri), Toast.LENGTH_LONG).show()
                }
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
                    startActivityForResult(takePicture, 1)
//                    Toast.makeText(this@ATaskManager, "Camera disabled", Toast.LENGTH_LONG).show()


                } else {

                    Toast.makeText(this@ATaskManager, "Camera permission needed", Toast.LENGTH_LONG).show()

                }
                return
            }

            PERMISSION_READ_GALLERY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    val pickPhoto = Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 0)
                } else {

                    Toast.makeText(this@ATaskManager, "External read permission needed", Toast.LENGTH_LONG).show()

                }
                return
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
            Log.d("time", "onTimeSet: hh:mm: $hour:$min")

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
}