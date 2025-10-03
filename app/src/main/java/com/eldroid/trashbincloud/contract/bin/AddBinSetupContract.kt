package com.eldroid.trashbincloud.contract.bin

import com.eldroid.trashbincloud.model.entity.bin.FoundBin

interface AddBinSetupContract {
    
    interface View {
        fun showBinDetails(bin: FoundBin)
        fun showAvailableWifiNetworks(networks: List<String>)
        fun showSelectedWifiNetwork(ssid: String)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(message: String)
        fun enableConnectButton(enabled: Boolean)
        fun clearPasswordField()
        fun navigateToSuccess()
        fun navigateBack()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadBinData(bin: FoundBin)
        fun scanWifiNetworks()
        fun onWifiNetworkSelected(ssid: String)
        fun onConnectClicked(password: String)
        fun onRefreshNetworks()
        fun onBackPressed()
    }
}