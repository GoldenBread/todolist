/*
package eu.epitech.levisse.thierry.todolist.task_management

import android.provider.MediaStore
import android.app.Activity.RESULT_OK
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri


*/
/**
 * Created by thierry on 04/02/18.
 *//*


class PicturePicker {

    fun getPathFromURI(contentUri: Uri, requestCode: Int, resultCode: Int, imageReturnedIntent: Intent): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = getActivity(, requestCode, imageReturnedIntent, resultCode).getContentResolver().query(contentUri, proj, "", null, "")
        if (cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 200) {
                val selectedImageUri = imageReturnedIntent.data
                if (null != selectedImageUri) {
                    val path = getPathFromURI(selectedImageUri, requestCode, resultCode, imageReturnedIntent)
                    DrawerAdapter.imageViewPP.setImageURI(selectedImageUri)
                }
            } else if (requestCode == 0) {
                val selectedImageUri = imageReturnedIntent.data
                if (null != selectedImageUri) {
                    val path = getPathFromURI(selectedImageUri, requestCode, resultCode, imageReturnedIntent)
                    //DrawerAdapter.imageViewPP.setImageURI(selectedImageUri);
                }
            }
        }
    }

    fun getPathFromURI(contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = getActivity().getContentResolver().query(contentUri, proj, "", null, "")
        if (cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }
}

*/
