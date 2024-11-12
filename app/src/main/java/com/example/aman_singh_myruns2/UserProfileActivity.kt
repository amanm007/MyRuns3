package com.example.aman_singh_myruns2

import android.Manifest // For handling camera and storage permissions
import android.content.pm.PackageManager // For checking if permissions are granted
import android.graphics.BitmapFactory // For decoding images into Bitmaps
import androidx.core.app.ActivityCompat // For requesting permissions at runtime
import androidx.core.content.ContextCompat // For checking permissions at runtime
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File



//REFERENCES:
//https://www.youtube.com/watch?v=FjrKMcnKahY&list=PLEtMn0Sw3XCdLmYfWmVn82gW1lP65E1CL
//https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics
//https://developer.android.com/reference/android/hardware/camera2/CameraManager


class UserProfileActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    //From CameraDemoKotlin
    //From Canvas
    private lateinit var imageView: ImageView
    private lateinit var changeButton: Button
    private lateinit var tempImgUri: Uri
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var myViewModel: MyViewModel
    private val tempImgFileName = "aman_temp_profile_img.jpg"
    private var line: String? = "..."
    private val TEXTVIEW_KEY = "textview_key"
    var isListenerActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)
        Util.checkPermissions(this)

        // Initializing our  SharedPreferences
        sharedPreferences = getSharedPreferences("ProfileData", MODE_PRIVATE)

        // Initializing our  UI elements
        val nameEditText = findViewById<EditText>(R.id.etNameLabel)
        val emailEditText = findViewById<EditText>(R.id.edetEmailLabel)
        val phoneEditText = findViewById<EditText>(R.id.etPhoneLabel)
        val classEditText = findViewById<EditText>(R.id.etClassLabel)
        val majorEditText = findViewById<EditText>(R.id.etMajorLabel)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)

        // Initializing the Save and Clear buttons
        val saveButton = findViewById<Button>(R.id.appsavelabel)
        val clearButton = findViewById<Button>(R.id.appclearlabel)

        // Initializing  ImageView and Change button for our profile pic
        imageView = findViewById(R.id.capturedImageView)
        changeButton = findViewById(R.id.changebuttonLabel)

        // Initializing our ViewModel
        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        // Set up camera functionality
        setupCamera()

        //Allow read and write permissions
        checkStoragePermissions()


        // Loading our  saved data
        loadData(nameEditText, emailEditText, phoneEditText, classEditText, majorEditText, genderRadioGroup)

        // Handling RadioGroup checked change
        var lastCheckedId = -1
        genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (isListenerActive) {
                isListenerActive = false
                if (lastCheckedId == checkedId) {
                    group.clearCheck() // Clear the radio button selection
                    lastCheckedId = -1
                } else {
                    lastCheckedId = checkedId
                }
                isListenerActive = true
            }
        }

        // Saving  profile data
        saveButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString("Name", nameEditText.text.toString())
            editor.putString("Email", emailEditText.text.toString())
            editor.putString("Phone", phoneEditText.text.toString())
            editor.putString("Class", classEditText.text.toString())
            editor.putString("Major", majorEditText.text.toString())
            editor.putInt("Gender", genderRadioGroup.checkedRadioButtonId)
            editor.apply()

            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
           finish()//go to previous activity
        }

        // Clearing the profile data
        clearButton.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            nameEditText.text.clear()
            emailEditText.text.clear()
            phoneEditText.text.clear()
            classEditText.text.clear()
            majorEditText.text.clear()

            // Temporarily disable the listener to prevent recursion
            isListenerActive = false
            genderRadioGroup.clearCheck() // This will clear the radio button selection without triggering the listener
            isListenerActive = true

            imageView.setImageResource(android.R.color.transparent)
            Toast.makeText(this, "Profile cleared!", Toast.LENGTH_SHORT).show()
            finish()//go to the previous activity
        }

        // Observe ViewModel for profile pic updates
        myViewModel.userImage.observe(this, { bitmap -> imageView.setImageBitmap(bitmap) })

        // Display previously captured image if it exists
        // From CameraDemoKotlin
        //From Canvas
        val tempImgFile = File(getExternalFilesDir(null), tempImgFileName)
        if (tempImgFile.exists()) {
            val bitmap = Util.getBitmap(this, tempImgUri) // Using the Util.kt method to load and rotate the bitmap
            imageView.setImageBitmap(bitmap)
        }
        else {
            imageView.setImageResource(android.R.color.transparent)
        }
    }


    // camera functionality
    //CameraKotlimDemo
    //From Canvas
    private fun setupCamera() {
        // Prepare the file for saving the image
        val tempImgFile = File(getExternalFilesDir(null), tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(this, "com.example.Aman_Singh_MyRuns2.fileprovider", tempImgFile)

        // Register for camera activity result
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val bitmap = Util.getBitmap(this, tempImgUri) // Use CameraDemoKotlin's Util method for rotating camera image
                myViewModel.userImage.value = bitmap
            }
        }

        val galleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri)) // No rotation needed for gallery images
                myViewModel.userImage.value = bitmap
            }
        }

        // "Change" button functionality
        changeButton.setOnClickListener {
            val options = arrayOf("Take a photo", "Choose from gallery")
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Select an option")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Launch camera intent
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
                        cameraResult.launch(intent)
                    }
                    1 -> {
                        // Launch gallery intent
                        galleryResult.launch("image/*")
                    }
                }
            }
            builder.show()
        }
    }

    // Handling storage permissions
    private fun checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
    }

    // Load previously saved data into the EditText fields
    private fun loadData(nameEditText: EditText, emailEditText: EditText, phoneEditText: EditText, classEditText: EditText, majorEditText: EditText, genderRadioGroup: RadioGroup) {
        val name = sharedPreferences.getString("Name", "")
        val email = sharedPreferences.getString("Email", "")
        val phone = sharedPreferences.getString("Phone", "")
        val clazz = sharedPreferences.getString("Class", "")
        val major = sharedPreferences.getString("Major", "")
        val genderId = sharedPreferences.getInt("Gender", -1)

        nameEditText.setText(name)
        emailEditText.setText(email)
        phoneEditText.setText(phone)
        classEditText.setText(clazz)
        majorEditText.setText(major)
        genderRadioGroup.check(genderId)
    }


}