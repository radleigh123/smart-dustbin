package com.eldroid.trashbincloud.presenter.bin

import android.content.Context
import android.util.Log
import com.eldroid.trashbincloud.contract.bin.AddBinScanningContract
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.model.repository.bin.ApConnectionRepository
import com.eldroid.trashbincloud.model.repository.bin.WifiScanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBinScanningPresenter(
    context: Context
) : AddBinScanningContract.Presenter {

    private var view: AddBinScanningContract.View? = null
    private val wifiScanRepository = WifiScanRepository(context)
    private val apConnectionRepository = ApConnectionRepository(context)
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    private val foundBins = mutableListOf<FoundBin>()

    override fun attachView(view: AddBinScanningContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun startWifiScan() {
        if (!wifiScanRepository.isWifiEnabled()) {
            view?.showWifiRequired()
            return
        }

        view?.showLoading()
        // Clear previous results before starting new scan
        foundBins.clear()
        view?.showFoundBins(emptyList())
        view?.updateFoundBinsCount(0)

        presenterScope.launch {
            try {
                wifiScanRepository.scanForDustbins().collect { result ->
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is WifiScanRepository.ScanResult.Success -> {
                                val bin = FoundBin(
                                    binId = result.wifiResult.BSSID ?: "unknown",
                                    name = result.wifiResult.SSID ?: "Unknown Dustbin",
                                    location = "Signal: ${result.wifiResult.level} dBm"
                                )
                                foundBins.add(bin)
                                // Create a new list to trigger ListAdapter's DiffUtil
                                view?.showFoundBins(foundBins.toList())
                                view?.updateFoundBinsCount(foundBins.size)
                            }
                            is WifiScanRepository.ScanResult.Complete -> {
                                view?.hideLoading()
                                if (result.count == 0) {
                                    view?.showError("No dustbins found. Make sure the dustbin is powered on and in AP mode.")
                                }
                                Log.d("AddBinScanningPresenter", "Scan complete. Found ${result.count} dustbin(s)")
                            }
                            is WifiScanRepository.ScanResult.Error -> {
                                view?.hideLoading()
                                view?.showError(result.message)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Scan failed: ${e.message}")
                    Log.e("AddBinScanningPresenter", "WiFi scan exception", e)
                }
            }
        }
    }

    override fun onBinSelected(bin: FoundBin) {
        Log.d("AddBinScanningPresenter", "Bin selected: ${bin.name}")
        view?.showApPasswordDialog(bin)
    }

    override fun onApPasswordEntered(bin: FoundBin, password: String) {
        if (password.isEmpty()) {
            view?.showError("Please enter AP password")
            return
        }

        view?.showLoading()

        presenterScope.launch {
            try {
                Log.d("AddBinScanningPresenter", "Attempting to connect to ${bin.name}")
                val result = apConnectionRepository.connectToAp(bin.name, password)

                withContext(Dispatchers.Main) {
                    view?.hideLoading()

                    result.onSuccess { message ->
                        Log.d("AddBinScanningPresenter", "Connected to AP: ${bin.name}")
                        view?.navigateToWifiSetup(bin)
                    }

                    result.onFailure { exception ->
                        val errorMessage = exception.message ?: "Unknown error"
                        
                        // Provide user-friendly error messages
                        val userMessage = when {
                            errorMessage.contains("deprecated", ignoreCase = true) ||
                            errorMessage.contains("Android version", ignoreCase = true) -> {
                                "Please connect to ${bin.name} manually through your device's WiFi settings, then return to this app and try again."
                            }
                            errorMessage.contains("timeout", ignoreCase = true) -> {
                                "Connection timeout. Make sure you're close to the dustbin and the password is correct."
                            }
                            errorMessage.contains("password", ignoreCase = true) -> {
                                "Incorrect password. Please check and try again."
                            }
                            errorMessage.contains("unavailable", ignoreCase = true) -> {
                                "Cannot find ${bin.name}. Make sure the dustbin is powered on and in AP mode."
                            }
                            else -> "Failed to connect: $errorMessage"
                        }
                        
                        view?.showError(userMessage)
                        Log.e("AddBinScanningPresenter", "AP connection failed: $errorMessage", exception)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("Connection error: ${e.message}")
                    Log.e("AddBinScanningPresenter", "AP connection exception", e)
                }
            }
        }
    }

    override fun onBackPressed() {
        apConnectionRepository.disconnectFromAp()
        view?.navigateBack()
    }

    override fun onPermissionGranted() {
        startWifiScan()
    }

    override fun onPermissionDenied() {
        view?.showLocationPermissionRequired()
    }
}