package com.example.aman_singh_myruns2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {

    val allEntries: LiveData<List<ExerciseEntry>> = repository.allEntries

    fun insert(entry: ExerciseEntry) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun delete(entry: ExerciseEntry) = viewModelScope.launch {
        repository.delete(entry)
    }
}