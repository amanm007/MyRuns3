package com.example.aman_singh_myruns2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager


object Util {
    fun checkPermissions(activity: Activity?) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT < 23) return
        //CameraDemoKotlun
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 0)
        }
        // Check for read storage permission (to access photos/videos)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Check for write storage permission
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), 100)
        }


    }



    //https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics
    //https://developer.android.com/reference/android/hardware/camera2/CameraManager
    //Getting appropiate camera oreintation
    //Credit to Borui Lui for linking these under the discussion post

    private fun getCamSensorOrientation(context: Context): Int {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cameraManager.cameraIdList

        for (cameraId in cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)

            // Use the back-facing camera
            if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
            }
        }
        return 0
    }


    fun getBitmap(context: Context, imgUri: Uri): Bitmap? {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        // Get the camera sensor orientation
        //    val sensorOrientation = getCamSensorOrientation(context)
        val matrix = Matrix()
        //    matrix.postRotate(sensorOrientation.toFloat())

        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

}