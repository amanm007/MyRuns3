package com.example.aman_singh_myruns2

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "exercise_entries")
@TypeConverters(Converters::class)
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val inputType: Int,
    val activityType: Int,
    val dateTime: Long, // Store as a timestamp
    val duration: Double,
    val distance: Double,
    val avgPace: Double,
    val avgSpeed: Double,
    val calorie: Double,
    val climb: Double,
    val heartRate: Double,
    val comment: String,
    val locationList: ByteArray,
    val routePoints: String

)