package com.eldroid.trashbincloud.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eldroid.trashbincloud.contract.BinDetailsContract
import com.eldroid.trashbincloud.databinding.FragmentBinDetailsBinding
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.presenter.BinDetailsPresenter


class BinDetailsFragment : Fragment(), BinDetailsContract.View {

    private var _binding: FragmentBinDetailsBinding? = null
    private val binding get() = _binding!!

    private val presenter: BinDetailsContract.Presenter = BinDetailsPresenter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBinDetailsBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        presenter.loadBinData()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    override fun showBinDetails(bin: TrashBin) {
        binding.tvBinName.text = bin.name
        binding.tvLocation.text = bin.location
        binding.tvFillLevel.text = "${bin.fillLevel}%"
        binding.progressBarCircular.progress = bin.fillLevel
        binding.tvLastEmptied.text = "Last Emptied: ${bin.lastEmptied}"
        binding.tvDaysToFill.text = "Days to fill: ${bin.daysToFill}"
    }

    override fun showError(message: String) {
        // Show a toast/snackbar/dialog
    }
}

private fun Any.setBackgroundColor(statusColor: Int) {
    TODO("Not yet implemented")
}

