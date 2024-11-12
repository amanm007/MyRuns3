package com.example.aman_singh_myruns2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataEntry : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var exerciseEntryDao: ExerciseEntryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_entry)

        // Initialize the database and DAO
        /*

        db = Room.databaseBuilder(

            applicationContext,
            AppDatabase::class.java, "exercise-database"
        ).build()
*/

        db = (application as MyApplication).database
        exerciseEntryDao = db.exerciseEntryDao()

        val datePickerEditText: EditText = findViewById(R.id.datePickerEditText)
        val timePickerEditText: EditText = findViewById(R.id.timePickerEditText)
        val durationEditText: EditText = findViewById(R.id.durationEditText)
        val distanceEditText: EditText = findViewById(R.id.distanceEditText)
        val caloriesEditText: EditText = findViewById(R.id.caloriesEditText)
        val heartRateEditText: EditText = findViewById(R.id.heartRateEditText)
        val commentsEditText: EditText = findViewById(R.id.commentsEditText)
        val activityTypeTextView: TextView = findViewById(R.id.activityTypeSpinner)
        val saveButton: Button = findViewById(R.id.saveButton)
        val cancelButton: Button= findViewById(R.id.cancelButton)

        // Populate the Spinner with the activity types

        val activityType = intent.getIntExtra("ACTIVITY_TYPE", -1)
        activityTypeTextView.text = getActivityTypeString(activityType)  // Convert to string for display


        datePickerEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, y, m, d ->
                val selectedDate = "$d/${m + 1}/$y"
                datePickerEditText.setText(selectedDate)
            }, year, month, day).show()
        }

        timePickerEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, h, m ->
                val selectedTime = "$h:$m"
                timePickerEditText.setText(selectedTime)
            }, hour, minute, true).show()
        }

        saveButton.setOnClickListener {
            val date = datePickerEditText.text.toString()
            val time = timePickerEditText.text.toString()
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateTime = dateTimeFormat.parse("$date $time")?.time ?: return@setOnClickListener

            val duration = durationEditText.text.toString().toDoubleOrNull() ?: 0.0
            val distance = distanceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val calories = caloriesEditText.text.toString().toIntOrNull() ?: 0
            val heartRate = heartRateEditText.text.toString().toIntOrNull() ?: 0
            val comments = commentsEditText.text.toString()

            // Create an ExerciseEntry object with all the collected data
            val exerciseEntry = ExerciseEntry(
                inputType = 0,
                activityType = activityType,
                dateTime = dateTime,
                duration = duration,
                distance = distance,
                avgPace = 0.0,
                avgSpeed = 0.0,
                calorie = calories.toDouble(),
                climb = 0.0,
                heartRate = heartRate.toDouble(),
                comment = comments,
                locationList = ByteArray(0)
            )


            saveEntryToDatabase(exerciseEntry)
            finish()

        }
        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry is canceled!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveEntryToDatabase(exerciseEntry: ExerciseEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                exerciseEntryDao.insertEntry(exerciseEntry)
                Log.d("DataEntry", "Entry saved: $exerciseEntry") // Log entry details

                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Entry saved!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DataEntry", "Error saving entry", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Failed to save entry", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getActivityTypeString(activityType: Int): String {
        return when (activityType) {
            0 -> "Running"
            1 -> "Walking"
            2-> "Standing"
            3-> "Cycling"
            4-> "Hiking"
            5-> "Downhill Skiing"
            6-> "Cross-Country Skiing"
            7-> "Snowboarding"
            8-> "Skating"
            9-> "Swimming"
            10-> "Mountain Biking"
            11-> "Wheelchair"
            12-> "Elliptical"
            else -> "Unknown"



        }
    }
}