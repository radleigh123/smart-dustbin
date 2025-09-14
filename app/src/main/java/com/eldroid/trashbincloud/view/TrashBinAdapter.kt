package com.eldroid.trashbincloud.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.entity.TrashBin

class TrashBinAdapter(
    private val onBinClickListener: (TrashBin) -> Unit
) : ListAdapter<TrashBin, TrashBinAdapter.BinViewHolder>(BinDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trash_bin, parent, false)
        return BinViewHolder(view, onBinClickListener)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Updates a specific bin in the list by finding its position by binId
     */
    fun updateBin(updatedBin: TrashBin) {
        val currentList = currentList.toMutableList()
        val position = currentList.indexOfFirst { it.binId == updatedBin.binId }

        if (position != -1) {
            currentList[position] = updatedBin
            submitList(currentList)
        }
    }

    class BinViewHolder(
        itemView: View,
        private val onBinClickListener: (TrashBin) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cardView: CardView = itemView.findViewById(R.id.card_trash_bin)
        private val nameText: TextView = itemView.findViewById(R.id.text_bin_name)
        private val locationText: TextView = itemView.findViewById(R.id.text_bin_location)
        private val statusText: TextView = itemView.findViewById(R.id.text_bin_status)
        private val fillLevelBar: ProgressBar = itemView.findViewById(R.id.progress_fill_level)
        private val fillLevelText: TextView = itemView.findViewById(R.id.text_fill_level)
        private val lastUpdatedText: TextView = itemView.findViewById(R.id.text_last_updated)
        private val batteryText: TextView = itemView.findViewById(R.id.text_battery)
        private val statusIndicator: ImageView = itemView.findViewById(R.id.image_status_indicator)

        fun bind(bin: TrashBin) {
            nameText.text = bin.name
            locationText.text = bin.location
            statusText.text = bin.status.capitalize()
            fillLevelBar.progress = bin.fillLevel
            fillLevelText.text = "${bin.fillLevel}%"
            lastUpdatedText.text = "Updated: ${bin.getFormattedTimestamp()}"
            batteryText.text = "Battery: ${bin.battery}%"

            // Set status color indicator
            statusIndicator.setColorFilter(bin.getStatusColor())

            // Set fill level color based on level
            /*
                        val fillColor = when {
                            bin.fillLevel >= 90 -> itemView.context.getColor(R.color.critical)
                            bin.fillLevel >= 70 -> itemView.context.getColor(R.color.warning)
                            else -> itemView.context.getColor(R.color.normal)
                        }
                        fillLevelBar.progressTintList = android.content.res.ColorStateList.valueOf(fillColor)
            */

            // Set click listener
            itemView.setOnClickListener { onBinClickListener(bin) }
        }
    }

    /**
     * DiffUtil class to efficiently update the RecyclerView
     */
    private class BinDiffCallback : DiffUtil.ItemCallback<TrashBin>() {
        override fun areItemsTheSame(oldItem: TrashBin, newItem: TrashBin): Boolean {
            return oldItem.binId == newItem.binId
        }

        override fun areContentsTheSame(oldItem: TrashBin, newItem: TrashBin): Boolean {
            return oldItem == newItem
        }
    }

}