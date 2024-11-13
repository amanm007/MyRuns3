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

class StartFragment: Fragment (){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        // Initializing our input type spinner
        val inputTypeSpinner: Spinner = view.findViewById(R.id.inputTypeSpinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.input_type_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            inputTypeSpinner.adapter = adapter
        }

        // Initializing our activityies type spinner
        val activityTypeSpinner: Spinner = view.findViewById(R.id.activityTypeSpinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activity_type_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityTypeSpinner.adapter = adapter
        }
        //to check if user selects Automatic or Gps and then hits start then ---> Gps MAP
        //if not the Manual Data Entry
        /*
        val startButton: Button = view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val selectedInputType = inputTypeSpinner.selectedItem.toString()
            val intent = when (selectedInputType) {
                "Automatic", "GPS" -> Intent(activity, MapDisplayActivity::class.java)
                "Manual Entry" -> Intent(activity, DataEntry::class.java)
                else -> null
            }
            intent?.let { startActivity(it) }
        }

         */
        val startButton: Button = view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val selectedInputType = inputTypeSpinner.selectedItem.toString()
            val selectedActivityTypePosition = activityTypeSpinner.selectedItemPosition

            val intent = when (selectedInputType) {
                "Automatic", "GPS" -> Intent(activity, MapDisplayActivity::class.java)
                "Manual Entry" -> {
                    // Create an intent for DataEntry and put the selected activity type position as an extra
                    Intent(activity, DataEntry::class.java).apply {
                        putExtra("ACTIVITY_TYPE", selectedActivityTypePosition)
                    }
                }
                else -> null
            }
            intent?.let { startActivity(it) }
        }

        return view
    }
}