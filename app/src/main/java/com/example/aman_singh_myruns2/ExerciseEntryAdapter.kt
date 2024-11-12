package com.example.aman_singh_myruns2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class ExerciseEntryAdapter(private val onClick: (ExerciseEntry) -> Unit) :
    ListAdapter<ExerciseEntry, ExerciseEntryAdapter.ExerciseEntryViewHolder>(ExerciseEntryDiffCallback) {

    class ExerciseEntryViewHolder(itemView: View, val onClick: (ExerciseEntry) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val activityTypeTextView: TextView =
            itemView.findViewById(R.id.activityTypeTextView)
        private val activityDateTimeTextView: TextView =
            itemView.findViewById(R.id.activityDateTimeTextView)
        private val distanceTextView: TextView = itemView.findViewById(R.id.distanceTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private var currentExerciseEntry: ExerciseEntry? = null

        init {
            itemView.setOnClickListener {
                currentExerciseEntry?.let {
                    onClick(it)
                }
            }
        }

        fun bind(entry: ExerciseEntry) {
            currentExerciseEntry = entry


            val date = Date(entry.dateTime)
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(date)
            val timeString = timeFormat.format(date)

            val activityTypeString = getActivityTypeString(entry.activityType)

            // Formating our distance and duration
            val distanceString = "${entry.distance} Kms"
            val hours = entry.duration.toInt() / 60
            val minutes = entry.duration.toInt() % 60
            val durationString = "${hours}hrs ${minutes}mins"


            // Now setting the text of the TextViews
            itemView.findViewById<TextView>(R.id.activityTypeTextView).text =
                "Activity: $activityTypeString"
            itemView.findViewById<TextView>(R.id.activityDateTimeTextView).text =
                "Date: $dateString, Time: $timeString"
            itemView.findViewById<TextView>(R.id.distanceTextView).text =
                "Distance: $distanceString"
            itemView.findViewById<TextView>(R.id.durationTextView).text =
                "Duration: $durationString"
        }

        private fun getActivityTypeString(activityType: Int): String {
            return when (activityType) {
                0 -> "Running"
                1 -> "Walking"
                2 -> "Standing"
                3 -> "Cycling"
                4 -> "Hiking"
                5 -> "Downhill Skiing"
                6 -> "Cross Country Skiing"
                7 -> "Snowboarding"
                8 -> "Skating"
                9 -> "Swimming"
                10 -> "Mountain Biking"
                11 -> "Wheelchair"
                12 -> "Elliptical"

                else -> "Unknown"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.exercise_entry_item, parent, false)
        return ExerciseEntryViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ExerciseEntryViewHolder, position: Int) {
        val entry = getItem(position)
        holder.bind(entry)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SavedEntryActivity::class.java).apply {
                putExtra("ENTRY_ID", entry.id) // Pass entry ID to the detail activity
            }
            context.startActivity(intent)
        }
    }

    object ExerciseEntryDiffCallback : DiffUtil.ItemCallback<ExerciseEntry>() {
        override fun areItemsTheSame(oldItem: ExerciseEntry, newItem: ExerciseEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExerciseEntry, newItem: ExerciseEntry): Boolean {
            return oldItem == newItem
        }
    }
}