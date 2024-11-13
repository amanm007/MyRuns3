package com.example.aman_singh_myruns2

import TrackingService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var polylineOptions: PolylineOptions
    private val routePoints = mutableListOf<LatLng>()
    private lateinit var activityTypeText: TextView
    private lateinit var avgSpeedText: TextView
    private lateinit var currentSpeedText: TextView
    private lateinit var climbText: TextView
    private lateinit var calorieText: TextView
    private lateinit var distanceText: TextView
    private var activityType: String? = null

    private var startTime: Long = 0L
    private var endTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapdisplay)

        activityType = intent.getStringExtra("ACTIVITY_TYPE") ?: "Unknown"

        activityTypeText = findViewById(R.id.activity_type)
        avgSpeedText = findViewById(R.id.avg_speed)
        currentSpeedText = findViewById(R.id.current_speed)
        climbText = findViewById(R.id.climb)
        calorieText = findViewById(R.id.calorie)
        distanceText = findViewById(R.id.distance)

        activityTypeText.text = "Type: $activityType"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        registerReceiver(locationReceiver, IntentFilter("ACTION_LOCATION_UPDATE"))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        polylineOptions = PolylineOptions().width(5f).color(Color.BLUE)
    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val speed = intent.getFloatExtra("speed", 0f)
            val location = LatLng(latitude, longitude)

            if (routePoints.isEmpty()) {
                map.addMarker(
                    MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("Start")
                )
                startTime = System.currentTimeMillis() // Record the start time on the first location update
            }

            routePoints.add(location)
            map.addPolyline(polylineOptions.add(location))

            currentSpeedText.text = "Cur speed: %.2f m/h".format(speed)
            distanceText.text = "Distance: %.2f Miles".format(calculateTotalDistance(routePoints))

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun startTracking() {
        val intent = Intent(this, TrackingService::class.java)
        startService(intent)
    }

    private fun stopTracking() {
        val intent = Intent(this, TrackingService::class.java)
        stopService(intent)

        if (routePoints.isNotEmpty()) {
            map.addMarker(
                MarkerOptions().position(routePoints.last())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("End")
            )
            endTime = System.currentTimeMillis() // Record the end time when tracking stops
        }

        val routePointsJson = Gson().toJson(routePoints)

        val exerciseEntry = ExerciseEntry(
            inputType = 2, // Assuming GPS/automatic
            activityType = 1, // Adjust to the relevant activity type
            dateTime = System.currentTimeMillis(),
            duration = calculateDuration(),
            distance = calculateTotalDistance(routePoints),
            avgPace = 0.0,
            avgSpeed = calculateAverageSpeed(routePoints),
            calorie = 0.0,
            climb = 0.0,
            heartRate = 0.0,
            comment = "",
            locationList = ByteArray(0),
            routePoints = routePointsJson
        )

        saveExerciseEntryToDatabase(exerciseEntry)
    }

    private fun saveExerciseEntryToDatabase(entry: ExerciseEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            (application as MyApplication).repository.insert(entry)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }

    private fun calculateDuration(): Double {
        return if (endTime > startTime) {
            (endTime - startTime) / (1000 * 60.0) // Convert milliseconds to minutes
        } else {
            0.0
        }
    }

    private fun calculateTotalDistance(points: List<LatLng>): Double {
        var totalDistance = 0.0
        for (i in 0 until points.size - 1) {
            val start = points[i]
            val end = points[i + 1]
            val result = FloatArray(1)
            android.location.Location.distanceBetween(
                start.latitude, start.longitude,
                end.latitude, end.longitude,
                result
            )
            totalDistance += result[0]
        }
        return totalDistance / 1609.34 // Convert meters to miles
    }

    private fun calculateAverageSpeed(points: List<LatLng>): Double {
        val totalDistance = calculateTotalDistance(points) // in miles
        val durationInHours = calculateDuration() / 60.0 // Convert minutes to hours
        return if (durationInHours > 0) {
            totalDistance / durationInHours // in miles per hour
        } else {
            0.0
        }
    }
}
