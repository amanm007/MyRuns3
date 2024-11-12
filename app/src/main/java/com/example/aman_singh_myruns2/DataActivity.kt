package com.example.aman_singh_myruns2

/*
class DataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)


        val datePickerEditText: EditText = findViewById(R.id.datePickerEditText)
        val timePickerEditText: EditText = findViewById(R.id.timePickerEditText)
        val saveButton: Button = findViewById(R.id.saveButton)
        val cancelButton: Button = findViewById(R.id.clearButton)



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
            // Show Time Picker Dialog
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            TimePickerDialog(this, { _, h, m ->
                val selectedTime = "$h:$m"
                timePickerEditText.setText(selectedTime)
            }, hour, minute, true).show()
        }
        saveButton.setOnClickListener {

            Toast.makeText(this, "Entry is saved!", Toast.LENGTH_SHORT).show()

            finish()
        }
        cancelButton.setOnClickListener {
            Toast.makeText(this, "Entry is canceled!", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}

 */