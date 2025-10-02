package com.eldroid.trashbincloud.model.repository.bin

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class WifiScanRepository(private val context: Context) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun isWifiEnabled(): Boolean {
        return wifiManager.isWifiEnabled
    }

    @SuppressLint("MissingPermission")
    fun scanForDustbins(): Flow<ScanResult> = callbackFlow {
        var isReceiverRegistered = false
        
        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, 
                    false
                ) ?: false

                Log.d("WifiScanRepository", "Scan completed. Success: $success")

                if (success) {
                    val results = wifiManager.scanResults
                    Log.d("WifiScanRepository", "Total networks found: ${results.size}")

                    val dustbins = results.filter {
                        !it.SSID.isNullOrEmpty() && 
                        it.SSID.startsWith("DUSTBIN", ignoreCase = true)
                    }

                    Log.d("WifiScanRepository", "Found ${dustbins.size} dustbin(s)")
                    
                    dustbins.forEach { result ->
                        trySend(ScanResult.Success(result))
                    }
                    
                    trySend(ScanResult.Complete(dustbins.size))
                } else {
                    Log.e("WifiScanRepository", "Scan failed")
                    trySend(ScanResult.Error("WiFi scan failed"))
                }

                // Unregister receiver after scan completes
                try {
                    if (isReceiverRegistered) {
                        context?.unregisterReceiver(this)
                        isReceiverRegistered = false
                        Log.d("WifiScanRepository", "Receiver unregistered in onReceive")
                    }
                } catch (e: IllegalArgumentException) {
                    Log.e("WifiScanRepository", "Receiver already unregistered in onReceive", e)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        
        try {
            context.registerReceiver(wifiScanReceiver, intentFilter)
            isReceiverRegistered = true
            Log.d("WifiScanRepository", "Receiver registered")
        } catch (e: Exception) {
            Log.e("WifiScanRepository", "Failed to register receiver", e)
            trySend(ScanResult.Error("Failed to register receiver: ${e.message}"))
        }

        val success = wifiManager.startScan()
        Log.d("WifiScanRepository", "Scan started: $success")

        if (!success) {
            trySend(ScanResult.Error("Scan throttled. Wait a few seconds and try again."))
        }

        awaitClose {
            try {
                if (isReceiverRegistered) {
                    context.unregisterReceiver(wifiScanReceiver)
                    isReceiverRegistered = false
                    Log.d("WifiScanRepository", "Receiver unregistered in awaitClose")
                }
            } catch (e: IllegalArgumentException) {
                Log.d("WifiScanRepository", "Receiver was already unregistered", e)
            }
        }
    }

    fun convertToFoundBins(scanResults: List<android.net.wifi.ScanResult>): List<FoundBin> {
        return scanResults.mapIndexed { index, result ->
            FoundBin(
                binId = result.BSSID ?: "unknown_$index",
                name = result.SSID ?: "Unknown Dustbin",
                location = "Signal: ${result.level} dBm"
            )
        }
    }

    sealed class ScanResult {
        data class Success(val wifiResult: android.net.wifi.ScanResult) : ScanResult()
        data class Complete(val count: Int) : ScanResult()
        data class Error(val message: String) : ScanResult()
    }
}