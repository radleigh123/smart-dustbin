package com.eldroid.trashbincloud.contract.bin

import com.eldroid.trashbincloud.model.entity.bin.FoundBin

interface AddBinScanningContract {
    
    interface View {
        fun showFoundBins(bins: List<FoundBin>)
        fun updateFoundBinCount(count: Int)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToSetup(bin: FoundBin)
        fun requestBluetoothPermissions()
        fun showBluetoothDisabled()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun startBleScanning()
        fun stopBleScanning()
        fun onBinSelected(bin: FoundBin)
        fun onRefreshClicked()
        fun onBackPressed()
        fun checkBluetoothPermissions(): Boolean
    }
}