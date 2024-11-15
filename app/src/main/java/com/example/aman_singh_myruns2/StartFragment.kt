package com.example.aman_singh_myruns2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        // Initializing input type spinner
        val inputTypeSpinner: Spinner = view.findViewById(R.id.inputTypeSpinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.input_type_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            inputTypeSpinner.adapter = adapter
        }

        // Initializing activity type spinner
        val activityTypeSpinner: Spinner = view.findViewById(R.id.activityTypeSpinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activity_type_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityTypeSpinner.adapter = adapter
        }

        // Set up start button click listener
        val startButton: Button = view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val selectedInputType = inputTypeSpinner.selectedItem.toString()
            val selectedActivityTypePosition = activityTypeSpinner.selectedItemPosition

            // Determine which activity to start based on selected input type
            val intent = when (selectedInputType) {
                "Automatic", "GPS" -> {
                    // Pass both input type and activity type to MapDisplayActivity
                    Intent(activity, MapDisplayActivity::class.java).apply {
                        putExtra("INPUT_TYPE", selectedInputType)
                        putExtra("ACTIVITY_TYPE", selectedActivityTypePosition)
                    }
                }
                "Manual Entry" -> {
                    // Pass the activity type to DataEntry
                    Intent(activity, DataEntry::class.java).apply {
                        putExtra("ACTIVITY_TYPE", selectedActivityTypePosition)
                    }
                }
                else -> null
            }

            // Start the appropriate activity
            intent?.let { startActivity(it) }
        }

        return view
    }
}
