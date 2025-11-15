package com.eldroid.trashbincloud.view

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.EventType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryAdapter(
    private var activities: MutableList<ActivityEvent>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_ACTIVITY_ITEM = 1

        @RequiresApi(Build.VERSION_CODES.O)
        private val dateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMM dd, yyyy")
        @RequiresApi(Build.VERSION_CODES.O)
        private val timeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm")
    }

    data class ListItem(
        val type: Int,
        val dateHeader: LocalDate? = null,
        val activity: ActivityEvent? = null
    )

    private var displayItems = mutableListOf<ListItem>()

    init {
        generateDisplayItems()
    }

    override fun getItemViewType(position: Int): Int {
        return displayItems[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_ACTIVITY_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_activity, parent, false)
                ActivityViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = displayItems[position]
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(item.dateHeader!!)
            is ActivityViewHolder -> holder.bind(item.activity!!)
        }
    }

    override fun getItemCount(): Int = displayItems.size

    fun updateActivities(newActivities: List<ActivityEvent>) {
        activities.clear()
        activities.addAll(newActivities)
        generateDisplayItems()
        notifyDataSetChanged()
    }

    private fun generateDisplayItems() {
        displayItems.clear()

        var currentDate: LocalDate? = null
        activities.forEach { activity ->
            val activityDate = activity.date
            if (activityDate != currentDate) {
                currentDate = activityDate
                displayItems.add(ListItem(TYPE_DATE_HEADER, dateHeader = activityDate))
            }
            displayItems.add(ListItem(TYPE_ACTIVITY_ITEM, activity = activity))
        }
    }

    inner class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.dateHeaderText)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(date: LocalDate) {
            dateText.text = date.format(dateFormatter)
        }
    }

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.activityIcon)
        private val titleTextView: TextView = itemView.findViewById(R.id.activityTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.activityDescription)
        private val detailTextView: TextView = itemView.findViewById(R.id.activityDetail)
        private val timeTextView: TextView = itemView.findViewById(R.id.activityTime)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(activity: ActivityEvent) {
            titleTextView.text = activity.title
            descriptionTextView.text = activity.description
            detailTextView.text = activity.detail
            timeTextView.text = activity.time.format(timeFormatter)

            iconImageView.setImageResource(activity.iconResource)
        }
    }

}
