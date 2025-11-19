package com.eldroid.trashbincloud.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.BinDetailsContract
import com.eldroid.trashbincloud.databinding.FragmentBinDetailsBinding
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.presenter.BinDetailsPresenter
import kotlin.getValue

class BinDetailsFragment : Fragment(), BinDetailsContract.View {

    private var _binding: FragmentBinDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: BinDetailsContract.Presenter
    private val args: BinDetailsFragmentArgs by navArgs() // Receive navigation arguments using Safe Args

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBinDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = BinDetailsPresenter(requireContext())
        presenter.attachView(this)

        // Setup menu button click listener
        setupMenuButton()

        // Setup back button
        binding.btnBack.setOnClickListener {
            // Handle back navigation
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Load bin data from arguments
        loadBinFromArguments()
    }

    private fun loadBinFromArguments() {
        val bin = args.bin
        presenter.loadBinData(bin)
    }

    private fun setupMenuButton() {
        binding.btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        // Use ContextThemeWrapper to apply custom style
        val wrapper = android.view.ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(R.menu.bin_details_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_details -> {
                    // Handle edit details click
                    Toast.makeText(
                        requireContext(),
                        "Edit Details clicked",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Navigate to edit screen or show edit dialog
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
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
        binding.progressBarCircular.progress = bin.fillLevel?.toInt() ?: 0
        binding.tvLastEmptied.text = bin.lastEmptied
        binding.tvDaysToFill.text = "${bin.daysToFill}"

        // Update fill percentage badge
        binding.tvFillPercentage.text = "${bin.fillLevel}%"

        // Update alert message based on fill level
        when {
            bin.fillLevel!! >= 76 -> {
                binding.tvAlertMessage.visibility = View.VISIBLE
                binding.tvAlertMessage.text = "⚠️ Bin is nearly full"
            }
            else -> {
                binding.tvAlertMessage.visibility = View.GONE
            }
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun findViewById(
        statusDot: Int,
        tvActivityTitle: Int,
        tvActivityTime: Int,
        ivInfo: Int
    ): BinDetailsContract.View {
        TODO("Not yet implemented")
    }
}