package com.eldroid.trashbincloud.presenter.bin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.eldroid.trashbincloud.contract.bin.AddBinScanningContract
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.model.repository.bin.BleProvisioningManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddBinScanningPresenter(
    private val context: Context
) : AddBinScanningContract.Presenter {

    private var view: AddBinScanningContract.View? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    private val foundBins = mutableListOf<FoundBin>()
    
    private val bleManager = BleProvisioningManager(context)

    override fun attachView(view: AddBinScanningContract.View) {
        this.view = view
        startBleScanning()
    }

    override fun detachView() {
        stopBleScanning()
        this.view = null
    }
    
    override fun startBleScanning() {
        if (!bleManager.isBluetoothEnabled()) {
            view?.showBluetoothDisabled()
            return
        }
        
        if (!checkBluetoothPermissions()) {
            view?.requestBluetoothPermissions()
            return
        }
        
        view?.showLoading()
        foundBins.clear()
        
        bleManager.startScanning { foundBin ->
            presenterScope.launch {
                // Avoid duplicates
                if (foundBins.none { it.binId == foundBin.binId }) {
                    foundBins.add(foundBin)
                    view?.showFoundBins(foundBins.toList())
                    view?.updateFoundBinCount(foundBins.size)
                    Log.d("AddBinScanningPresenter", "Found bin: ${foundBin.name}")
                }
            }
        }
        
        // Auto-stop scanning after 30 seconds
        presenterScope.launch {
            delay(30000)
            stopBleScanning()
            view?.hideLoading()
        }
    }
    
    override fun stopBleScanning() {
        bleManager.stopScanning()
        view?.hideLoading()
    }
    
    override fun onBinSelected(bin: FoundBin) {
        stopBleScanning()
        view?.navigateToSetup(bin)
    }
    
    override fun onRefreshClicked() {
        startBleScanning()
    }
    
    override fun onBackPressed() {
        stopBleScanning()
    }
    
    override fun checkBluetoothPermissions(): Boolean {
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
        
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}