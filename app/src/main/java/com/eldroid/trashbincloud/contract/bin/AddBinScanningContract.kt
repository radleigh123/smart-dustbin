package com.eldroid.trashbincloud.contract.bin

import com.eldroid.trashbincloud.model.entity.bin.FoundBin

interface AddBinScanningContract {
    
    interface View {
        fun showFoundBins(bins: List<FoundBin>)
        fun updateFoundBinsCount(count: Int)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showLocationPermissionRequired()
        fun showLocationServicesRequired()
        fun showWifiRequired()
        fun navigateToWifiSetup(bin: FoundBin)
        fun navigateBack()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun startWifiScan()
        fun onBinSelected(bin: FoundBin)
        fun onBackPressed()
        fun onPermissionGranted()
        fun onPermissionDenied()
    }
}