package com.example.aman_singh_myruns2


import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SavedEntryActivity : AppCompatActivity() {
    private lateinit var entry: ExerciseEntry
    private var entryId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_entry)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Activity Details"

        entryId = intent.getLongExtra("ENTRY_ID", -1L)
        if (entryId == -1L) {
            Toast.makeText(this, "Entry not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Fetch entry details to display
        loadEntryDetails(entryId)

        // Delete button click listener
        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            deleteEntry(entryId)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Close the activity and go back
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadEntryDetails(id: Long) {
        lifecycleScope.launch {
            val entry = withContext(Dispatchers.IO) {
                (application as MyApplication).repository.getEntryById(id)
            }

            if (entry != null) {
                displayEntryDetails(entry)
            } else {
                Toast.makeText(this@SavedEntryActivity, "Entry not found", Toast.LENGTH_SHORT)
                    .show()
                finish() // Close activity if entry not found
            }
        }
    }

    private fun deleteEntry(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val entry = (application as MyApplication).repository.getEntryById(id)
            if (entry != null) {
                (application as MyApplication).repository.delete(entry)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedEntryActivity, "Entry deleted", Toast.LENGTH_SHORT)
                        .show()
                    finish() // Close the activity after deletion
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SavedEntryActivity,
                        "Failed to delete entry",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun displayEntryDetails(entry: ExerciseEntry) {
        findViewById<TextView>(R.id.inputTypeTextView).text =
            "Input Type: ${getInputTypeText(entry.inputType)}"
        findViewById<TextView>(R.id.activityTypeTextView).text =
            "Activity Type: ${getActivityTypeText(entry.activityType)}"
        findViewById<TextView>(R.id.dateTimeTextView).text =
            "Date and Time: ${formatDateTime(entry.dateTime)}"
        findViewById<TextView>(R.id.durationTextView).text = "Duration: ${entry.duration} mins"
        findViewById<TextView>(R.id.distanceTextView).text = "Distance: ${entry.distance} km"
        findViewById<TextView>(R.id.caloriesTextView).text = "Calories: ${entry.calorie} kcal"
        findViewById<TextView>(R.id.heartRateTextView).text = "Heart Rate: ${entry.heartRate} bpm"
        findViewById<TextView>(R.id.commentsTextView).text = "Comments: ${entry.comment}"
    }

    private fun getInputTypeText(inputType: Int): String {
        return when (inputType) {
            0 -> "Manual"
            1 -> "GPS"
            2 -> "Automatic"
            else -> "Unknown"
        }
    }

    private fun getActivityTypeText(activityType: Int): String {
        return when (activityType) {
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
    }

    private fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}