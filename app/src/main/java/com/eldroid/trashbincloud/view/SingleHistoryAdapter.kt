package com.eldroid.trashbincloud.view

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.databinding.ItemActivityBinding
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import java.time.format.DateTimeFormatter

class SingleHistoryAdapter(
    private var activities: List<ActivityEvent>
): RecyclerView.Adapter<SingleHistoryAdapter.SingleHistoryViewHolder>() {

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

    private lateinit var binding: ItemActivityBinding

    inner class SingleHistoryViewHolder(val binding: ItemActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(activity: ActivityEvent) {
            binding.activityTitle.text = activity.title
            binding.activityDescription.text = activity.description  // property, not getDescription()
            binding.activityDetail.text = activity.detail  // property, not getDetail()
            binding.activityTime.text = activity.time?.format(timeFormatter)  // property, not getTime()

            activity.icon?.let {
                binding.activityIcon.setImageResource(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleHistoryViewHolder {
        binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleHistoryViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: SingleHistoryViewHolder,
        position: Int) {
        holder.bind(activities[position])
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateActivities(newActivities: List<ActivityEvent>) {
        activities = newActivities
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = activities.size
}