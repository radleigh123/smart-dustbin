package com.eldroid.trashbincloud.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.entity.BinActivity

class BinRecentActivityAdapter(private val items: List<BinActivity>) :
    RecyclerView.Adapter<BinRecentActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusDot: View = view.findViewById(R.id.statusDot)
        val title: TextView = view.findViewById(R.id.tvActivityTitle)
        val time: TextView = view.findViewById(R.id.tvActivityTime)
        val infoIcon: ImageView = view.findViewById(R.id.ivInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bin_item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.time.text = item.time
        holder.statusDot.setBackgroundResource(item.statusColorRes)

        // Optional: handle info icon click
        holder.infoIcon.setOnClickListener {
            // TODO: show details or dialog
        }
    }

    override fun getItemCount() = items.size
}
