package com.eldroid.trashbincloud.view.bin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eldroid.trashbincloud.contract.bin.AddBinScanningContract
import com.eldroid.trashbincloud.databinding.FragmentAddBinScanningBinding
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.presenter.bin.AddBinScanningPresenter
import com.eldroid.trashbincloud.view.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddBinScanningFragment : Fragment(), AddBinScanningContract.View {

    private var _binding: FragmentAddBinScanningBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AddBinScanningContract.Presenter

    private val addBinScanningAdapter = AddBinScanningAdapter { bin ->
        presenter.onBinSelected(bin)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinScanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = AddBinScanningPresenter(requireContext())
        presenter.attachView(this)

        setupRecyclerView()
        setupClickListeners()
        checkPermissionAndScan()
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            presenter.onBackPressed()
        }

        binding.refreshBtn.setOnClickListener {
            checkPermissionAndScan()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerFoundBins.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addBinScanningAdapter
        }
    }

    private fun checkPermissionAndScan() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                if (isLocationEnabled()) {
                    presenter.startWifiScan()
                } else {
                    showLocationServicesRequired()
                }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationale()
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(LocationManager::class.java)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Permission Required")
            .setMessage("WiFi scanning requires location permission to detect nearby dustbins.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                presenter.onPermissionDenied()
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    presenter.onPermissionGranted()
                } else {
                    showLocationServicesRequired()
                }
            } else {
                presenter.onPermissionDenied()
            }
        }
    }

    // View Interface Implementation
    override fun showFoundBins(bins: List<FoundBin>) {
        addBinScanningAdapter.submitList(bins.toList()) {
            if (bins.isEmpty()) {
                binding.recyclerFoundBins.visibility = View.GONE
            } else {
                binding.recyclerFoundBins.visibility = View.VISIBLE
            }
        }
    }

    override fun updateFoundBinsCount(count: Int) {
        binding.tvFoundDevices.text = "Found Devices ($count)"
    }

    override fun showLoading() {
        binding.tvFoundDevices.text = "Scanning..."
        binding.refreshBtn.isEnabled = false
    }

    override fun hideLoading() {
        binding.refreshBtn.isEnabled = true
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showLocationPermissionRequired() {
        Toast.makeText(
            requireContext(),
            "Location permission is required to scan for dustbins",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun showLocationServicesRequired() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Services Required")
            .setMessage("WiFi scanning requires location services to be enabled. Please enable location services.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Location services are required for WiFi scanning",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    override fun showWifiRequired() {
        Toast.makeText(requireContext(), "Please enable WiFi", Toast.LENGTH_SHORT).show()
    }

    override fun showApPasswordDialog(bin: FoundBin) {
        val passwordInput = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Enter AP password"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Connect to ${bin.name}")
            .setMessage("Enter the Access Point password for this dustbin")
            .setView(passwordInput)
            .setPositiveButton("Connect") { dialog, _ ->
                val password = passwordInput.text.toString()
                presenter.onApPasswordEntered(bin, password)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun navigateToWifiSetup(bin: FoundBin) {
        val action = AddBinScanningFragmentDirections
            .actionAddBinScanningFragmentToAddBinSetupFragment(bin)
        findNavController().navigate(action)

        Log.d("AddBinScanningFragment", "Navigating to WiFi setup for bin: ${bin.name}")
    }

    override fun navigateBack() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}