package com.example.aman_singh_myruns2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExerciseEntryDao {
    @Insert
     fun insertEntry(entry: ExerciseEntry)

    @Delete
     fun deleteEntry(entry: ExerciseEntry)

    @Query("SELECT * FROM exercise_entries WHERE id = :id")
     fun getEntryById(id: Long): ExerciseEntry

    @Query("DELETE FROM exercise_entries WHERE id = :id")
     fun deleteEntryById(id: Long)

    @Query("SELECT * FROM exercise_entries")
     fun getAllEntries(): LiveData<List<ExerciseEntry>>
}