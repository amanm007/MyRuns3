package com.example.aman_singh_myruns2


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MapDisplayActivity : AppCompatActivity() {

    //Only Initialising both Gps/Automatic fragments with Save and Cancel Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapdisplay)

        // Save button implementation
        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            Toast.makeText(this, "Entry is saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Cancel button implementation
        val cancelButton: Button = findViewById(R.id.clearButton)
        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry is canceled", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
