package com.eldroid.trashbincloud.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
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
//                binding.tvFillLevel.text = "${bin.fillLevel ?: 0}%"

                val fill = bin.fillLevel ?: 0
                binding.tvFillLevel.text = "$fill%"

                // Change progress
                binding.progressBar.progress = fill

                // ===== COLOR + STATUS BASED ON FILL LEVEL =====
                val (statusText, statusColor) = when {
                    fill == 100 -> Pair("FULL", R.color.red)
                    fill >= 80 -> Pair("CRITICAL", R.color.red)
                    fill >= 50 -> Pair("WARNING", R.color.orange)
                    fill > 30 -> Pair("NORMAL", R.color.green)
                    else -> Pair("LOW", R.color.green)
                }

                // Progress bar color
                binding.progressBar.progressTintList =
                    android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(binding.root.context, statusColor)
                    )

                // Fill percentage text color
                binding.tvFillLevel.setTextColor(
                    ContextCompat.getColor(binding.root.context, statusColor)
                )

                // ===== BIN STATUS TEXT + COLOR =====
                binding.tvBinStatus.text = statusText

                // ⚠️ IMPORTANT: use backgroundTint, NOT setBackgroundColor
                binding.tvBinStatus.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, statusColor)

                // === NEW: Change progress bar color based on fill level ===
//                val color = when {
//                    fill > 80 -> ContextCompat.getColor(binding.root.context, R.color.red)
//                    fill > 50 -> ContextCompat.getColor(binding.root.context, R.color.orange)
//                    fill > 30 -> ContextCompat.getColor(binding.root.context, R.color.green)
//                    else -> ContextCompat.getColor(binding.root.context, R.color.green)
//                }
//
//                binding.progressBar.progressTintList =
//                    android.content.res.ColorStateList.valueOf(color)
//
//                binding.tvFillLevel.setTextColor(color)
//
//
//                binding.tvBinStatus.background = when (bin.status) {
//                    0 -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.background_text)
//                    1 -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.bg_fill_status_orange)
//                    2 -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.bg_fill_status_red)
//                    else -> binding.tvBinStatus.context.getDrawable(com.eldroid.trashbincloud.R.drawable.gradient_button_bg)
//                }
//                binding.progressBar.progress = bin.fillLevel ?: 0

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