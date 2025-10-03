package com.eldroid.trashbincloud.presenter.bin

import android.content.Context
import android.util.Log
import com.eldroid.trashbincloud.contract.bin.AddBinScanningContract
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
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
        view?.navigateToWifiSetup(bin)
    }

    override fun onBackPressed() {
        view?.navigateBack()
    }

    override fun onPermissionGranted() {
        startWifiScan()
    }

    override fun onPermissionDenied() {
        view?.showLocationPermissionRequired()
    }
}