package com.eldroid.trashbincloud.view.bin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R

class WifiNetworkAdapter(
    private val onNetworkClickListener: (String) -> Unit
) : ListAdapter<String, WifiNetworkAdapter.WifiViewHolder>(WifiDiffCallback()) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wifi, parent, false)
        return WifiViewHolder(view, onNetworkClickListener)
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    fun setSelectedNetwork(ssid: String) {
        val newPosition = currentList.indexOfFirst { it == ssid }
        if (newPosition != -1) {
            val oldPosition = selectedPosition
            selectedPosition = newPosition
            
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }
            notifyItemChanged(newPosition)
        }
    }

    class WifiViewHolder(
        itemView: View,
        private val onNetworkClickListener: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val wifiNameText: TextView = itemView.findViewById(R.id.wifi_name_text)

        fun bind(ssid: String, isSelected: Boolean) {
            wifiNameText.text = ssid
            
            // Update background based on selection
            itemView.alpha = if (isSelected) 1.0f else 0.7f
            
            itemView.setOnClickListener {
                onNetworkClickListener(ssid)
            }
        }
    }

    private class WifiDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}