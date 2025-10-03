package com.eldroid.trashbincloud.model.repository.bin

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import com.eldroid.trashbincloud.retrofit.NetworkClient
import com.eldroid.trashbincloud.retrofit.WifiCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WifiSetupRepository(private val context: Context) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    suspend fun sendWifiCredentials(ssid: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val wifiCredentials = WifiCredentials(ssid, password)
                val response = NetworkClient.apiService.setWifiCredentials(wifiCredentials)
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("WifiSetupRepository", "WiFi credentials sent successfully: ${responseBody?.status}")
                    Result.success(responseBody?.status ?: "Success")
                } else {
                    Log.e("WifiSetupRepository", "Failed to send WiFi credentials: ${response.code()}")
                    Result.failure(Exception("Failed to send WiFi credentials: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("WifiSetupRepository", "Error sending WiFi credentials", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getAvailableNetworks(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val wifiScanRepository = WifiScanRepository(context)
                // Get current scan results
                val scanResults = wifiManager.scanResults
                
                // Filter out duplicates and empty SSIDs
                scanResults
                    .filter { !it.SSID.isNullOrEmpty() }
                    .map { it.SSID }
                    .distinct()
                    .sorted()
            } catch (e: Exception) {
                Log.e("WifiSetupRepository", "Error getting available networks", e)
                emptyList()
            }
        }
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8 // WPA/WPA2 minimum
    }
}