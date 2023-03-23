package com.example.camera

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

class PermissionManager private constructor() {
    private var context: Context? = null
    private fun init(context: Context) {
        this.context = context
    }

    fun checkPermissions(permissions: Array<String>): Boolean {
        val size = permissions.size
        for (i in 0 until size) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    permissions[i]
                ) == PermissionChecker.PERMISSION_DENIED
            ) {
                return false
            }
        }
        return true
    }

    fun askPermissions(activity: Activity?, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity!!, permissions, requestCode)
    }

    fun handlePermissionResult(
        grantResults: IntArray
    ) {
        if (grantResults.size > 0) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(activity, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(activity, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    companion object {
        private var instance: PermissionManager? = null
        fun getInstance(context: Context): PermissionManager? {
            if (instance == null) {
                instance = PermissionManager()
            }
            instance!!.init(context)
            return instance
        }
    }
}