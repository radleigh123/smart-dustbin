package com.eldroid.trashbincloud.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.BinDetailsContract
import com.eldroid.trashbincloud.databinding.FragmentBinDetailsBinding
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.model.repository.ActivityRepository
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.TrashBinRepository
import com.eldroid.trashbincloud.presenter.BinDetailsPresenter
import com.google.android.material.textfield.TextInputEditText

class BinDetailsFragment : Fragment(), BinDetailsContract.View {

    private var _binding: FragmentBinDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepository
    private lateinit var actRepository: ActivityRepository
    private lateinit var binRepository: TrashBinRepository
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var activityAdapter: SingleHistoryAdapter
    private lateinit var presenter: BinDetailsContract.Presenter
    private val args: BinDetailsFragmentArgs by navArgs()

    // Store current bin
    private var currentBin: TrashBin? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBinDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository()
        actRepository = ActivityRepository()
        binRepository = TrashBinRepository()

        presenter = BinDetailsPresenter(requireContext(), authRepository, actRepository)
        presenter.attachView(this)

        activityRecyclerView = binding.rvRecentActivity

        setupMenuButton()
        setupBackButton()
        setupRecyclerView()
        loadBinFromArguments()
    }

    private fun loadBinFromArguments() {
        val bin = args.bin
        currentBin = bin
        presenter.loadBinData(bin)
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMenuButton() {
        binding.btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun setupRecyclerView() {
        activityAdapter = SingleHistoryAdapter(emptyList())
        activityRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activityAdapter
        }
    }

    private fun showPopupMenu(view: View) {
        val wrapper = android.view.ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(R.menu.bin_details_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_details -> {
                    showEditBinDialog()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showEditBinDialog() {
        currentBin?.let { bin ->
            // Create custom dialog layout
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_bin, null)

            val etBinName = dialogView.findViewById<TextInputEditText>(R.id.etBinName)
            val etLocation = dialogView.findViewById<TextInputEditText>(R.id.etLocation)

            // Pre-fill with current values
            etBinName.setText(bin.name)
            etLocation.setText(bin.location)

            // Build the dialog
            AlertDialog.Builder(requireContext())
                .setTitle("Edit Bin Details")
                .setView(dialogView)
                .setPositiveButton("Save") { dialog, _ ->
                    val newName = etBinName.text.toString().trim()
                    val newLocation = etLocation.text.toString().trim()

                    if (newName.isEmpty()) {
                        Toast.makeText(requireContext(), "Bin name cannot be empty", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    if (newLocation.isEmpty()) {
                        Toast.makeText(requireContext(), "Location cannot be empty", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Update bin details
                    updateBinDetails(newName, newLocation)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun updateBinDetails(newName: String, newLocation: String) {
        currentBin?.let { bin ->
            // Update the bin object
            bin.name = newName
            bin.location = newLocation

            // Update in Firebase
            val userUid = authRepository.currentUserId()
            userUid?.let { uid ->
                binRepository.updateBin(uid, bin) { success, error ->
                    if (success) {
                        Toast.makeText(requireContext(), "Bin details updated successfully", Toast.LENGTH_SHORT).show()
                        // Update UI
                        binding.tvBinName.text = newName
                        binding.tvLocation.text = newLocation
                    } else {
                        Toast.makeText(requireContext(), "Failed to update: ${error ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    override fun showBinDetails(bin: TrashBin) {
        val fill = bin.fillLevel ?: 0

        binding.tvBinName.text = bin.name ?: "UNNAMED BIN"
        binding.tvLocation.text = bin.location ?: "UNKNOWN"
        binding.tvFillLevel.text = "$fill%"
        binding.tvFillPercentage.text = "$fill%"

        // Circular progress
        binding.progressBarCircular.progress = fill

        // ===== COLOR + STATUS BASED ON FILL LEVEL =====
        val (statusText, statusColor) = when {
            fill == 100 -> Pair("FULL", R.color.red)
            fill >= 80 -> Pair("CRITICAL", R.color.red)
            fill >= 50 -> Pair("WARNING", R.color.orange)
            fill > 30 -> Pair("NORMAL", R.color.green)
            else -> Pair("LOW", R.color.green)
        }

        val context = requireContext()

        // Base color (solid)
        val baseColor = ContextCompat.getColor(context, statusColor)

        // 🔥 80% opacity background (204 = ~80%)
        val bgColorWithOpacity =
            androidx.core.graphics.ColorUtils.setAlphaComponent(baseColor, 77)

        // Progress color (solid)
        binding.progressBarCircular.progressTintList =
            android.content.res.ColorStateList.valueOf(baseColor)

        // Percentage text color (solid)
        binding.tvFillPercentage.setTextColor(baseColor)

        // Rounded background with 80% opacity
        binding.tvFillPercentage.backgroundTintList =
            android.content.res.ColorStateList.valueOf(bgColorWithOpacity)

        // ===== ALERT MESSAGE =====
        if (fill >= 80) {
            binding.tvAlertMessage.visibility = View.VISIBLE
            binding.tvAlertMessage.text = "⚠️ Bin is nearly full ($statusText)"
        } else {
            binding.tvAlertMessage.visibility = View.GONE
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

    override fun showActivities(activities: List<ActivityEvent>) {
        if (activities.isEmpty()) {
            binding.rvRecentActivity.visibility = View.GONE
            // You can show empty state here if you added tvNoActivities in layout
        } else {
            binding.rvRecentActivity.visibility = View.VISIBLE
            activityAdapter.updateActivities(activities)
        }
    }

    override fun showNoActivities(activities: List<ActivityEvent>) {
        binding.rvRecentActivity.visibility = View.GONE
        // Show empty state message
        Toast.makeText(requireContext(), "No recent activities", Toast.LENGTH_SHORT).show()
    }
}