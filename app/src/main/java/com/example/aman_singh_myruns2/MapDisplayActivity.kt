import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aman_singh_myruns2.ExerciseEntry
import com.example.aman_singh_myruns2.MyApplication
import com.example.aman_singh_myruns2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

public class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var polylineOptions: PolylineOptions
    private val routePoints = mutableListOf<LatLng>() // Store route points

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapdisplay)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.start_tracking).setOnClickListener {
            startTracking()
        }
        findViewById<Button>(R.id.stop_tracking).setOnClickListener {
            stopTracking()
        }

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

            routePoints.add(location) // Add point to the route
            map.addMarker(MarkerOptions().position(location))
            polylineOptions.add(location)
            map.addPolyline(polylineOptions)

            findViewById<TextView>(R.id.speed).text = "Speed: $speed m/s"
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

        // Convert route points to JSON using Gson
        val routePointsJson = Gson().toJson(routePoints)

        // Create an ExerciseEntry with route points
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
            locationList = ByteArray(0), // Update as needed
            routePoints = routePointsJson
        )

        // Save to the database
        saveExerciseEntryToDatabase(exerciseEntry)
    }

    private fun saveExerciseEntryToDatabase(entry: ExerciseEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            // Access your database and insert entry
            (application as MyApplication).repository.insert(entry)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationReceiver)
    }

    private fun calculateDuration(): Double {
        // Calculate duration based on tracking start and stop times
        return 0.0 // Implement as needed
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
        return totalDistance
    }

    private fun calculateAverageSpeed(points: List<LatLng>): Double {
        // Calculate average speed based on duration and distance
        return 0.0 // Implement as needed
    }
}
