package com.example.aman_singh_myruns2
import android.app.Application
import androidx.room.Room


class MyApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "exercise-database").build()
    }

    val repository: ExerciseRepository by lazy {
        ExerciseRepository(database.exerciseEntryDao())
    }
}