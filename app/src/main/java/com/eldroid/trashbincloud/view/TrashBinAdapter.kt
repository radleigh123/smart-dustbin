package com.eldroid.trashbincloud.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.databinding.ItemBinBinding
import com.eldroid.trashbincloud.model.entity.TrashBin

class TrashBinAdapter(
    private var bins: List<TrashBin>,
    private val onBinClick: (TrashBin) -> Unit = {}
): RecyclerView.Adapter<TrashBinAdapter.TrashBinViewHolder>() {

    inner class TrashBinViewHolder(val binding: ItemBinBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashBinViewHolder {
        val binding = ItemBinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrashBinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrashBinViewHolder, position: Int) {
        val bin = bins[position]
        with(holder.binding) {
            tvBinName.text = bin.name ?: "UNNAMED BIN"
            tvBinLocation.text = bin.location ?: "UNKNOWN XYZ"
            tvFillLevel.text = "${bin.fillLevel ?: 0}%"
            tvBinStatus.background = when (bin.status) {
                0 -> tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.background_text)
                1 -> tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.bg_fill_status_orange)
                2 -> tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.bg_fill_status_red)
                else -> tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.gradient_button_bg)
            }
            progressBar.progress = bin.fillLevel ?: 0

            // Set click listener on the entire card
            root.setOnClickListener {
                onBinClick(bin)
            }
        }
    }

    override fun getItemCount(): Int = bins.size

    fun updateBins(newBins: List<TrashBin>) {
        bins = newBins
        notifyDataSetChanged()
    }
}