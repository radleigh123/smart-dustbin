package com.eldroid.trashbincloud.presenter.bin

import android.content.Context
import android.util.Log
import com.eldroid.trashbincloud.contract.bin.AddBinSetupContract
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.bin.BleProvisioningManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AddBinSetupPresenter(
    private val context: Context
) : AddBinSetupContract.Presenter {

    private var view: AddBinSetupContract.View? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    
    private var currentBin: FoundBin? = null
    private var selectedSsid: String? = null
    private var bleManager: BleProvisioningManager? = null

    override fun attachView(view: AddBinSetupContract.View) {
        this.view = view
    }

    override fun detachView() {
        bleManager?.disconnect()
        bleManager = null
        this.view = null
    }

    override fun loadBinData(bin: FoundBin) {
        currentBin = bin
        view?.showBinDetails(bin)
        
        // Connect to device first
        connectAndScanWifi(bin)
    }

    private fun connectAndScanWifi(bin: FoundBin) {
        view?.showLoading()
        bleManager = BleProvisioningManager(context)
        
        bleManager?.connectToDevice(
            deviceAddress = bin.binId,
            onConnectionChanged = { connected ->
                if (!connected) {
                    presenterScope.launch {
                        view?.hideLoading()
                        view?.showError("Failed to connect to device")
                    }
                }
            },
            onReady = {
                // NOW services are ready, request WiFi networks
                Log.d("AddBinSetupPresenter", "Services ready, requesting WiFi scan")
                bleManager?.requestWifiNetworks { networks ->
                    presenterScope.launch {
                        view?.hideLoading()
                        if (networks.isNotEmpty()) {
                            view?.showAvailableWifiNetworks(networks)
                        } else {
                            view?.showError("No WiFi networks found")
                        }
                    }
                }
            }
        )
    }

    override fun scanWifiNetworks() {
        val bin = currentBin ?: return
        connectAndScanWifi(bin)
    }

    override fun onWifiNetworkSelected(ssid: String) {
        selectedSsid = ssid
        view?.showSelectedWifiNetwork(ssid)
        view?.enableConnectButton(true)
        Log.d("AddBinSetupPresenter", "WiFi network selected: $ssid")
    }

    override fun onConnectClicked(password: String) {
        val ssid = selectedSsid

        if (ssid == null) {
            view?.showError("Please select a WiFi network")
            return
        }
        
        if (password.isBlank()) {
            view?.showError("Please enter WiFi password")
            return
        }

        val authRepo = AuthRepository()
        val userUid = "user_" + authRepo.currentUser()?.uid.toString()
        val binId = "bin_${UUID.randomUUID()}"
        
        view?.showLoading()
        
        bleManager?.provisionWifi(ssid, password, userUid, binId) { success, error ->
            presenterScope.launch {
                if (success) {
                    view?.hideLoading()
                    view?.showSuccess("WiFi credentials sent successfully!")
                    view?.navigateToSuccess()
                } else {
                    view?.hideLoading()
                    view?.showError(error ?: "Failed to provision WiFi")
                }
                bleManager?.disconnect()
            }
        }
    }

    override fun onRefreshNetworks() {
        selectedSsid = null
        view?.enableConnectButton(false)
        scanWifiNetworks()
    }

    override fun onBackPressed() {
        bleManager?.disconnect()
        view?.navigateBack()
    }
}