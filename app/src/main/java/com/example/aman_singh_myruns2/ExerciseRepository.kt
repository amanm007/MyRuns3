package com.example.aman_singh_myruns2

import android.util.Log
import androidx.lifecycle.LiveData

class ExerciseRepository(private val exerciseEntryDao: ExerciseEntryDao) {

    val allEntries: LiveData<List<ExerciseEntry>> = exerciseEntryDao.getAllEntries()

    suspend fun insert(entry: ExerciseEntry) {
        exerciseEntryDao.insertEntry(entry)
        val entries = exerciseEntryDao.getAllEntries()
        //for documentation and testing
        Log.d("ExerciseRepository", "Current entries after insert: $entries")
    }

    suspend fun delete(entry: ExerciseEntry) {
        exerciseEntryDao.deleteEntry(entry)
    }

    suspend fun getEntryById(id: Long): ExerciseEntry? {
        return exerciseEntryDao.getEntryById(id)
    }
}
