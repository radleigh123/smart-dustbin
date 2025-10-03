package com.eldroid.trashbincloud.view.bin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.eldroid.trashbincloud.contract.bin.AddBinSetupContract
import com.eldroid.trashbincloud.databinding.FragmentAddBinSetupBinding
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.presenter.bin.AddBinSetupPresenter
import com.eldroid.trashbincloud.view.MainActivity

class AddBinSetupFragment : Fragment(), AddBinSetupContract.View {

    private var _binding: FragmentAddBinSetupBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AddBinSetupContract.Presenter
    
    // Receive navigation arguments using Safe Args
    private val args: AddBinSetupFragmentArgs by navArgs()
    
    private val wifiNetworkAdapter = WifiNetworkAdapter { ssid ->
        presenter.onWifiNetworkSelected(ssid)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = AddBinSetupPresenter(requireContext())
        presenter.attachView(this)

        setupRecyclerView()
        setupClickListeners()
        
        // Load bin data from navigation arguments
        loadBinFromArguments()
    }

    private fun setupRecyclerView() {
        binding.recyclerWifi.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wifiNetworkAdapter
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            presenter.onBackPressed()
        }

        binding.refreshBtn.setOnClickListener {
            presenter.onRefreshNetworks()
        }

        binding.connectBtn.setOnClickListener {
            val password = binding.outlinedTextField.editText?.text.toString()
            presenter.onConnectClicked(password)
        }

        // Disable connect button initially
        binding.connectBtn.isEnabled = false
    }

    private fun loadBinFromArguments() {
        // Get the selected bin from Safe Args
        val selectedBin = args.selectedBin
        presenter.loadBinData(selectedBin)
    }

    // View Interface Implementation
    override fun showBinDetails(bin: FoundBin) {
        binding.cardHeroBinName.text = bin.name
        binding.cardHeroBinDetails.text = "Signal: ${bin.location}"
    }

    override fun showAvailableWifiNetworks(networks: List<String>) {
        wifiNetworkAdapter.submitList(networks)
    }

    override fun showSelectedWifiNetwork(ssid: String) {
        wifiNetworkAdapter.setSelectedNetwork(ssid)
        Toast.makeText(requireContext(), "Selected: $ssid", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        binding.connectBtn.isEnabled = false
        binding.refreshBtn.isEnabled = false
    }

    override fun hideLoading() {
        binding.refreshBtn.isEnabled = true
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun enableConnectButton(enabled: Boolean) {
        binding.connectBtn.isEnabled = enabled
    }

    override fun clearPasswordField() {
        binding.outlinedTextField.editText?.text?.clear()
    }

    override fun navigateToSuccess() {
        Toast.makeText(requireContext(), "Bin successfully configured!", Toast.LENGTH_LONG).show()
        navigateBack()
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