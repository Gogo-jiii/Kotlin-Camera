package com.example.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.camera.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CameraManager.CameraCallback {

    private lateinit var binding: ActivityMainBinding
    private var permissionManager: PermissionManager? = null
    private val permissions = arrayOf<String>(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var cameraManager: CameraManager? = null
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        permissionManager = PermissionManager.getInstance(this)
        cameraManager = CameraManager.getInstance(this)

        binding.btnClickPhoto.setOnClickListener {
            if (!permissionManager!!.checkPermissions(permissions)) {
                permissionManager!!.askPermissions(this@MainActivity, permissions, 100)
            } else {
                //permission granted
                cameraManager!!.openCamera()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CameraManager.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Log.d("TAG", "1")
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
                //imageView.setImageBitmap(bitmap);
                cameraManager!!.setPic(binding.imageView)
                cameraManager!!.addPicToGallery(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            permissionManager!!.handlePermissionResult(
                grantResults
            )

            //permission granted
            cameraManager!!.openCamera()
        }
    }


    override fun getPhotoUri(
        photoUri: Uri?,
        takePictureIntent: Intent?,
        REQUEST_IMAGE_CAPTURE: Int
    ) {
        this.photoUri = photoUri
        Log.d("TAG", "2")
        if (takePictureIntent != null) {
            Log.d("TAG", "3")
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
}