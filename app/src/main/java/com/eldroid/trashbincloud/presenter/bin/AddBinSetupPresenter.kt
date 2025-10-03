package com.eldroid.trashbincloud.presenter.bin

import android.content.Context
import android.util.Log
import com.eldroid.trashbincloud.contract.bin.AddBinSetupContract
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.model.repository.bin.WifiSetupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBinSetupPresenter(
    context: Context
) : AddBinSetupContract.Presenter {

    private var view: AddBinSetupContract.View? = null
    private val repository = WifiSetupRepository(context)
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    
    private var currentBin: FoundBin? = null
    private var selectedSsid: String? = null

    override fun attachView(view: AddBinSetupContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadBinData(bin: FoundBin) {
        currentBin = bin
        view?.showBinDetails(bin)
        // Automatically scan for networks when bin data is loaded
        scanWifiNetworks()
    }

    override fun scanWifiNetworks() {
        view?.showLoading()
        presenterScope.launch {
            try {
                val networks = withContext(Dispatchers.IO) {
                    repository.getAvailableNetworks()
                }
                
                view?.hideLoading()
                if (networks.isNotEmpty()) {
                    view?.showAvailableWifiNetworks(networks)
                } else {
                    view?.showError("No WiFi networks found. Please check WiFi is enabled.")
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError("Failed to scan WiFi networks: ${e.message}")
                Log.e("AddBinSetupPresenter", "Error scanning WiFi networks", e)
            }
        }
    }

    override fun onWifiNetworkSelected(ssid: String) {
        selectedSsid = ssid
        view?.showSelectedWifiNetwork(ssid)
        view?.enableConnectButton(true)
        Log.d("AddBinSetupPresenter", "WiFi network selected: $ssid")
    }

    override fun onConnectClicked(password: String) {
        val ssid = selectedSsid
        
        if (ssid.isNullOrEmpty()) {
            view?.showError("Please select a WiFi network")
            return
        }

        if (password.isEmpty()) {
            view?.showError("Please enter WiFi password")
            return
        }

        if (!repository.validatePassword(password)) {
            view?.showError("Password must be at least 8 characters")
            return
        }

        view?.showLoading()
        view?.enableConnectButton(false)

        presenterScope.launch {
            try {
                val result = repository.sendWifiCredentials(ssid, password)
                
                view?.hideLoading()
                
                result.onSuccess { message ->
                    view?.showSuccess("WiFi credentials sent successfully!")
                    view?.clearPasswordField()
                    // Navigate to success after a short delay
                    kotlinx.coroutines.delay(1500)
                    view?.navigateToSuccess()
                }
                
                result.onFailure { exception ->
                    view?.showError("Failed to send WiFi credentials: ${exception.message}")
                    view?.enableConnectButton(true)
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError("Connection failed: ${e.message}")
                view?.enableConnectButton(true)
                Log.e("AddBinSetupPresenter", "Error connecting to WiFi", e)
            }
        }
    }

    override fun onRefreshNetworks() {
        selectedSsid = null
        view?.enableConnectButton(false)
        scanWifiNetworks()
    }

    override fun onBackPressed() {
        view?.navigateBack()
    }
}