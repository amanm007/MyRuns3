package com.example.aman_singh_myruns2

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SavedEntryActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var entry: ExerciseEntry
    private var entryId: Long = -1L
    private lateinit var map: GoogleMap
    private var showMap: Boolean = false // Tracks if map should be shown

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_entry)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Activity Details"

        // Retrieve entry ID
        entryId = intent.getLongExtra("ENTRY_ID", -1L)
        if (entryId == -1L) {
            Toast.makeText(this, "Entry not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load entry details to determine if map should be shown
        loadEntryDetails(entryId)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this) // Set the callback for the map

        // Delete button action
        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            deleteEntry(entryId)
        }
    }

    // Handle back navigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Load entry details and set visibility for the map
    private fun loadEntryDetails(id: Long) {
        lifecycleScope.launch {
            val entry = withContext(Dispatchers.IO) {
                (application as MyApplication).repository.getEntryById(id)
            }

            if (entry != null) {
                displayEntryDetails(entry)

                // Show map if input type is GPS or Automatic
                showMap = entry.inputType == 1 || entry.inputType == 2
                findViewById<View>(R.id.mapContainer).visibility = if (showMap) View.VISIBLE else View.GONE
            } else {
                Toast.makeText(this@SavedEntryActivity, "Entry not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Only display route if input type is GPS or Automatic
        if (showMap && ::entry.isInitialized) {
            displayRouteOnMap(entry)
        }
    }

    // Display route on map with start and end markers
    private fun displayRouteOnMap(entry: ExerciseEntry) {
        val routePoints: List<LatLng> = Gson().fromJson(entry.routePoints, Array<LatLng>::class.java).toList()
        if (routePoints.isNotEmpty()) {
            val polylineOptions = PolylineOptions().addAll(routePoints).width(5f).color(Color.BLUE)
            map.addPolyline(polylineOptions)

            // Add start and end markers
            map.addMarker(
                MarkerOptions().position(routePoints.first())
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            map.addMarker(
                MarkerOptions().position(routePoints.last())
                    .title("End")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            // Move the camera to the start of the route
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(routePoints.first(), 15f))
        }
    }

    // Delete the entry from the database
    private fun deleteEntry(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val entry = (application as MyApplication).repository.getEntryById(id)
            if (entry != null) {
                (application as MyApplication).repository.delete(entry)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedEntryActivity, "Entry deleted", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after deletion
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedEntryActivity, "Failed to delete entry", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Display entry details in the UI
    private fun displayEntryDetails(entry: ExerciseEntry) {
        findViewById<TextView>(R.id.inputTypeTextView).text = "Input Type: ${getInputTypeText(entry.inputType)}"
        findViewById<TextView>(R.id.activityTypeTextView).text = "Activity Type: ${getActivityTypeText(entry.activityType)}"
        findViewById<TextView>(R.id.dateTimeTextView).text = "Date and Time: ${formatDateTime(entry.dateTime)}"
        findViewById<TextView>(R.id.durationTextView).text = "Duration: ${entry.duration} mins"
        findViewById<TextView>(R.id.distanceTextView).text = "Distance: ${entry.distance} km"
        findViewById<TextView>(R.id.caloriesTextView).text = "Calories: ${entry.calorie} kcal"
        findViewById<TextView>(R.id.heartRateTextView).text = "Heart Rate: ${entry.heartRate} bpm"
        findViewById<TextView>(R.id.commentsTextView).text = "Comments: ${entry.comment}"
    }

    // Utility functions for formatting
    private fun getInputTypeText(inputType: Int): String = when (inputType) {
        0 -> "Manual"
        1 -> "GPS"
        2 -> "Automatic"
        else -> "Unknown"
    }

    private fun getActivityTypeText(activityType: Int): String = when (activityType) {
        0 -> "Running"
        1 -> "Walking"
        2 -> "Standing"
        3 -> "Cycling"
        4 -> "Hiking"
        5 -> "Downhill Skiing"
        6 -> "Cross Country Skiing"
        7 -> "Snowboarding"
        8 -> "Skating"
        9 -> "Swimming"
        10 -> "Mountain Biking"
        11 -> "Wheelchair"
        12 -> "Elliptical"
        else -> "Unknown"
    }

    private fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
