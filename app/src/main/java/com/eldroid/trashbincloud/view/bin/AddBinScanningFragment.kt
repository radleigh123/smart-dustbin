package com.eldroid.trashbincloud.view.bin

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.bin.AddBinScanningContract
import com.eldroid.trashbincloud.databinding.FragmentAddBinScanningBinding
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.presenter.bin.AddBinScanningPresenter

class AddBinScanningFragment : Fragment(), AddBinScanningContract.View {

    private var _binding: FragmentAddBinScanningBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AddBinScanningContract.Presenter

    private val addBinScanningAdapter = AddBinScanningAdapter { bin ->
        presenter.onBinSelected(bin)
    }

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            presenter.startBleScanning()
        } else {
            showError("Bluetooth permissions are required to scan for devices")
        }
    }
    
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            presenter.startBleScanning()
        }
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
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            presenter.onBackPressed()
            // findNavController().navigateUp()
            findNavController().popBackStack()
        }

        binding.refreshBtn.setOnClickListener {
            presenter.onRefreshClicked()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerFoundBins.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addBinScanningAdapter
        }
    }

    // View Interface Implementation
    override fun showFoundBins(bins: List<FoundBin>) {
        addBinScanningAdapter.submitList(bins)
    }

    override fun updateFoundBinCount(count: Int) {
        binding.tvFoundDevices.text = "Found Devices ($count)"
    }

    override fun showLoading() {
        binding.refreshBtn.isEnabled = false
        binding.tvFoundDevices.text = "Scanning..."
    }

    override fun hideLoading() {
        binding.refreshBtn.isEnabled = true
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToSetup(bin: FoundBin) {
        val action = AddBinScanningFragmentDirections
            .actionAddBinScanningFragmentToAddBinSetupFragment(bin)
        findNavController().navigate(action)
    }

    override fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        bluetoothPermissionLauncher.launch(permissions)
    }

    override fun showBluetoothDisabled() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(enableBtIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }
}