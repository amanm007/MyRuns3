package com.example.aman_singh_myruns2

//REFERENCES:
//https://www.youtube.com/watch?v=pOKPQ8rYe6g&list=PLHQRWugvckFrWppucVnQ6XhiJyDbaCU79

//https://www.youtube.com/watch?v=g-YnGyBdV-s&list=PLHQRWugvckFrWppucVnQ6XhiJyDbaCU79&index=2
//https://www.youtube.com/watch?v=XimcwP-OzFg&list=PLHQRWugvckFrWppucVnQ6XhiJyDbaCU79&index=3
//https://www.youtube.com/watch?v=p2T6w06j_eE&list=PLHQRWugvckFrWppucVnQ6XhiJyDbaCU79&index=4
//https://www.youtube.com/watch?v=9Va14Q6edD8&list=PLHQRWugvckFrWppucVnQ6XhiJyDbaCU79&index=7
//AP KEY   AIzaSyDSixL-GEnJweVWJhWHHgsJmCKb9EaAJx8


import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class TrackingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                Log.d("TrackingService", "Location update: ${location.latitude}, ${location.longitude}")
                sendLocationUpdate(location)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        Log.d("TrackingService", "Service created and location request configured.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackingService", "Service started.")
        startLocationUpdates()
        return START_STICKY
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
            Log.d("TrackingService", "Location updates started.")
        } else {
            Log.e("TrackingService", "Location permission not granted.")
        }
    }

    private fun sendLocationUpdate(location: Location) {
        val intent = Intent("ACTION_LOCATION_UPDATE").apply {
            putExtra("latitude", location.latitude)
            putExtra("longitude", location.longitude)
            putExtra("speed", location.speed)
        }
        Log.d("TrackingService", "Broadcasting location: ${location.latitude}, ${location.longitude}")
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("TrackingService", "Service destroyed and location updates stopped.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
