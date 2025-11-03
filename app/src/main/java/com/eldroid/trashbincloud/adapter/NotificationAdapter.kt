package com.eldroid.trashbincloud.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.entity.Notification

class NotificationAdapter(
    private var notificationList: List<Notification>,
    private val onItemClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotifViewHolder>() {

    inner class NotifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvBody: TextView = itemView.findViewById(R.id.tvBody)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_notification, parent, false)
        return NotifViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val notif = notificationList[position]
        holder.tvTitle.text = notif.title
        holder.tvBody.text = notif.body
        holder.tvTime.text = notif.getFormattedTimestamp()
        holder.tvType.text = notif.type ?: "Info"

        val context = holder.itemView.context
        when (notif.type?.lowercase()) {
            "info" -> {
                holder.tvType.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_700))
                holder.ivIcon.setImageResource(R.drawable.hold_bg)
            }
            "urgent" -> {
                holder.tvType.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                holder.ivIcon.setImageResource(R.drawable.circle_red)
            }
            "warning" -> {
                holder.tvType.setBackgroundColor(ContextCompat.getColor(context, R.color.button_hold))
                holder.ivIcon.setImageResource(R.drawable.ic_auto_open)
            }
            else -> {
                holder.tvType.setBackgroundColor(ContextCompat.getColor(context, R.color.button_close))
                holder.ivIcon.setImageResource(R.drawable.notification_icon)
            }
        }

        // Dim the text if already read
        if (notif.isRead == true) {
            holder.tvTitle.alpha = 0.5f
            holder.tvBody.alpha = 0.5f
        } else {
            holder.tvTitle.alpha = 1f
            holder.tvBody.alpha = 1f
        }

        holder.itemView.setOnClickListener {
            onItemClick(notif)
        }
    }

    override fun getItemCount() = notificationList.size

    fun updateList(newList: List<Notification>) {
        notificationList = newList
        notifyDataSetChanged()
    }
}
