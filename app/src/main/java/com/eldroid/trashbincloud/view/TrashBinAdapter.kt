package com.eldroid.trashbincloud.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.databinding.ItemBinBinding
import com.eldroid.trashbincloud.model.entity.TrashBin

class TrashBinAdapter(
    private var bins: List<TrashBin>
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
            progressBar.progress = bin.distance ?: 0
        }
    }

    override fun getItemCount(): Int = bins.size

    fun updateBins(newBins: List<TrashBin>) {
        bins = newBins
        notifyDataSetChanged()
    }

}