package com.example.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraManager private constructor() {
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var cameraCallback: CameraCallback? = null
    private var context: Context? = null
    private fun init(context: Context) {
        this.context = context
        cameraCallback = context as CameraCallback
    }

    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context!!.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(
                    context!!,
                    context!!.packageName + ".fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                cameraCallback!!.getPhotoUri(photoUri, takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    fun addPicToGallery(bitmap: Bitmap?) {
        MediaStore.Images.Media.insertImage(
            context!!.contentResolver, bitmap, "Title",
            "Description"
        )
    }

    fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW = imageView.width
        val targetH = imageView.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        imageView.setImageBitmap(bitmap)
    }

    internal interface CameraCallback {
        fun getPhotoUri(
            photoUri: Uri?, takePictureIntent: Intent?,
            REQUEST_IMAGE_CAPTURE: Int
        )
    }

    companion object {
        private var instance: CameraManager? = null
        var REQUEST_IMAGE_CAPTURE = 1
        fun getInstance(context: Context): CameraManager? {
            if (instance == null) {
                instance = CameraManager()
            }
            instance!!.init(context)
            return instance
        }
    }
}