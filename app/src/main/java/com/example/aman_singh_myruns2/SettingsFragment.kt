package com.example.aman_singh_myruns2


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.net.Uri

class SettingsFragment : Fragment ()

{
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("SettingsData", Context.MODE_PRIVATE)


        val privacyCheckBox = view.findViewById<CheckBox>(R.id.privacyCheckBox)
        val privacyHintTextView = view.findViewById<TextView>(R.id.privacyHintTextView)
        val userProfileHintTextView = view.findViewById<TextView>(R.id.userProfileHintTextView)
        val accountInfoText = view.findViewById<TextView>(R.id.accountInfoText)
        val unitPreferenceTextView = view.findViewById<TextView>(R.id.unitPreferenceTextView)
        val commentsTextView = view.findViewById<TextView>(R.id.commentsTextView)
        val linkTextView = view.findViewById<TextView>(R.id.linkTextView)


        userProfileHintTextView.visibility = View.VISIBLE


        if (privacyCheckBox.isChecked) {
            privacyHintTextView.visibility = View.VISIBLE
        } else {
            privacyHintTextView.visibility = View.GONE
        }


        privacyCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                privacyHintTextView.visibility = View.VISIBLE
            } else {
                privacyHintTextView.visibility = View.GONE
            }
        }
        linkTextView.setOnClickListener {
            val webLink = "https://www.sfu.ca/computing.html" // our sfu link
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webLink))
            startActivity(intent)
        }

        accountInfoText.setOnClickListener {
            val intent = Intent(activity, UserProfileActivity::class.java)
           startActivity(intent)
        }
        unitPreferenceTextView.setOnClickListener {
            showUnitPreferenceDialog()
        }

        commentsTextView.setOnClickListener {
            showCommentsDialog()
        }
    }

    private fun showUnitPreferenceDialog() {
        val unitOptions = arrayOf("KM/s", "M/s")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Unit Preference")

        val checkedItem = sharedPreferences.getInt("UnitPreference", 0) // Load saved choices
        builder.setSingleChoiceItems(unitOptions, checkedItem) { dialog, which ->

            sharedPreferences.edit().putInt("UnitPreference", which).apply() // Save choice
            Toast.makeText(requireContext(), "Selected: ${unitOptions[which]}", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        //cancel button for to go backkl
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }
    private fun showCommentsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter Comments")

        val input = EditText(requireContext())
        input.setText(sharedPreferences.getString("Comments", "")) // Loading our  saved comment
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->

            val comment = input.text.toString()
            sharedPreferences.edit().putString("Comments", comment).apply() // Save comment
            Toast.makeText(requireContext(), "Comment: $comment", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
