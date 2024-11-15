package com.example.aman_singh_myruns2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
    private var startTime: Long = 0L
    private var endTime: Long = 0L

    private lateinit var activityTypeText: TextView
    private lateinit var avgSpeedText: TextView
    private lateinit var currentSpeedText: TextView
    private lateinit var climbText: TextView
    private lateinit var calorieText: TextView
    private lateinit var distanceText: TextView
    private var activityType: Int = 0

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapdisplay)

        activityType = intent.getIntExtra("ACTIVITY_TYPE", 0)
        val activityTypes = resources.getStringArray(R.array.activity_type_options)
        activityTypeText = findViewById(R.id.activity_type)
        activityTypeText.text = "Type: ${activityTypes.getOrNull(activityType) ?: "Unknown"}"

        // Initializig our  TextViews
        avgSpeedText = findViewById(R.id.avg_speed)
        currentSpeedText = findViewById(R.id.current_speed)
        climbText = findViewById(R.id.climb)
        calorieText = findViewById(R.id.calorie)
        distanceText = findViewById(R.id.distance)

        // Set up map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            Log.d("MapDisplayActivity", "Save button clicked.")
            stopTracking()
            saveExerciseEntry()
        }

        findViewById<Button>(R.id.clearButton).setOnClickListener {
            Log.d("MapDisplayActivity", "Cancel button clicked.")
            stopTracking()
            finish()
        }

        // Request permissions if not granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initializeTracking()
        }
    }

    private fun initializeTracking() {
        startTracking()
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

            Log.d("MapDisplayActivity", "Received location update: $latitude, $longitude")

            // our google maps markers
            if (routePoints.isEmpty()) {
                map.addMarker(
                    MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("Start")
                )
                startTime = System.currentTimeMillis()
            }

            routePoints.add(location)
            polylineOptions.add(location)
            map.addPolyline(polylineOptions)

            // our info for our Google maps and Automatic/GPS Input Types
            currentSpeedText.text = "Current speed: %.2f m/h".format(speed)
            distanceText.text = "Distance: %.2f Miles".format(calculateTotalDistance(routePoints))
            avgSpeedText.text = "Avg speed: %.2f m/h".format(calculateAverageSpeed(routePoints))

            // putting our map into the the centre
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun startTracking() {
        val intent = Intent(this, TrackingService::class.java)
        startService(intent)
        Log.d("MapDisplayActivity", "Started TrackingService")
    }

    private fun stopTracking() {
        val intent = Intent(this, TrackingService::class.java)
        stopService(intent)
        Log.d("MapDisplayActivity", "Stopped TrackingService")

        // Add the end red marker if we have route points
        if (routePoints.isNotEmpty()) {
            map.addMarker(
                MarkerOptions().position(routePoints.last())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("End")
            )
            endTime = System.currentTimeMillis()
        }
    }

    private fun saveExerciseEntry() {
        if (routePoints.isEmpty()) {
            Toast.makeText(this, "No route points to save.", Toast.LENGTH_SHORT).show()
            return
        }

        val routePointsJson = Gson().toJson(routePoints)
        val exerciseEntry = ExerciseEntry(
            inputType = 2,
            activityType = activityType,
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                (application as MyApplication).repository.insert(exerciseEntry)
                launch(Dispatchers.Main) {
                    //testing our saving entries and deleting entries with log statments
                    Toast.makeText(this@MapDisplayActivity, "Entry saved!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MapDisplayActivity", "Failed to save ExerciseEntry", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MapDisplayActivity, "Failed to save entry.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }

    private fun calculateDuration(): Double {
        return if (endTime > startTime) {
            (endTime - startTime) / (1000 * 60.0)
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
        return totalDistance / 1609.34
    }

    private fun calculateAverageSpeed(points: List<LatLng>): Double {
        val totalDistance = calculateTotalDistance(points)
        val durationInHours = calculateDuration() / 60.0
        return if (durationInHours > 0) {
            totalDistance / durationInHours
        } else {
            0.0
        }
    }
}
