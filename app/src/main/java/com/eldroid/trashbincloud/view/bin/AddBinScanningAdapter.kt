package com.eldroid.trashbincloud.view.bin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.entity.bin.FoundBin

class AddBinScanningAdapter(
    private val onBinClickListener: (FoundBin) -> Unit
): ListAdapter<FoundBin, AddBinScanningAdapter.BinViewHolder>(BinDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_found_bin, parent, false)
        return BinViewHolder(view, onBinClickListener)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Updates a specific bin in the list by finding its position by binId
     */
    fun updateBin(updatedBin: FoundBin) {
        val currentList = currentList.toMutableList()
        val position = currentList.indexOfFirst { it.binId == updatedBin.binId }

        if (position != -1) {
            currentList[position] = updatedBin
            submitList(currentList)
        }
    }

    class BinViewHolder(
        itemView: View,
        private val onBinClickListener: (FoundBin) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val nameText: TextView = itemView.findViewById(R.id.item_bin_name)
        private val selectButton: AppCompatButton = itemView.findViewById(R.id.item_button)

        fun bind(bin: FoundBin) {
            nameText.text = bin.name

            // Set click listener on the button
            selectButton.setOnClickListener {
                onBinClickListener(bin)
            }
        }
    }

    /**
     * DiffUtil class to efficiently update the RecyclerView
     */
    private class BinDiffCallback : DiffUtil.ItemCallback<FoundBin>() {
        override fun areItemsTheSame(oldItem: FoundBin, newItem: FoundBin): Boolean {
            return oldItem.binId == newItem.binId
        }

        override fun areContentsTheSame(oldItem: FoundBin, newItem: FoundBin): Boolean {
            return oldItem == newItem
        }
    }

}