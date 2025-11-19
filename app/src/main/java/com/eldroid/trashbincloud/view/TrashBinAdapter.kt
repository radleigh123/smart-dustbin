package com.eldroid.trashbincloud.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.databinding.ItemBinBinding
import com.eldroid.trashbincloud.model.entity.TrashBin

class TrashBinAdapter(
    private var bins: List<TrashBin>,
    private val onBinClick: (TrashBin) -> Unit,
    private val onOpenBtnClick: (TrashBin) -> Unit,
    private val onCloseBtnClick: (TrashBin) -> Unit,
    private val onHoldBtnClick: (TrashBin) -> Unit
): RecyclerView.Adapter<TrashBinAdapter.TrashBinViewHolder>() {

    private lateinit var binding: ItemBinBinding

    inner class TrashBinViewHolder(val binding: ItemBinBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(bin: TrashBin) {
                binding.tvBinName.text = bin.name ?: "UNNAMED BIN"
                binding.tvBinLocation.text = bin.location ?: "UNKNOWN XYZ"
                binding.tvFillLevel.text = "${bin.fillLevel ?: 0}%"
                binding.tvBinStatus.background = when (bin.status) {
                    0 -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.background_text)
                    1 -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.bg_fill_status_orange)
                    2 -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.bg_fill_status_red)
                    else -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.gradient_button_bg)
                }
                binding.progressBar.progress = bin.fillLevel ?: 0

                // Setup click listeners
                setupClickListeners(bin)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashBinViewHolder {
        binding = ItemBinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrashBinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrashBinViewHolder, position: Int) {
        holder.bind(bins[position])
    }

    private fun setupClickListeners(bin: TrashBin) {
        // Listener on the entire card
        binding.root.setOnClickListener { onBinClick(bin) }

        binding.btnOpen.setOnClickListener {
            Log.d("TrashBinAdapter", "OPEN clicked!")
            onOpenBtnClick(bin)
        }

        binding.btnClose.setOnClickListener {
            Log.d("TrashBinAdapter", "CLOSE clicked!")
            onCloseBtnClick(bin)
        }

        binding.btnHold.setOnClickListener {
            Log.d("TrashBinAdapter", "HOLD clicked!")
            onHoldBtnClick(bin)
        }
    }

    fun updateBins(newBins: List<TrashBin>) {
        bins = newBins
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = bins.size
}