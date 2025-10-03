package com.eldroid.trashbincloud.model.repository.bin

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import com.eldroid.trashbincloud.retrofit.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class ApConnectionRepository(private val context: Context) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    suspend fun connectToAp(ssid: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ApConnectionRepository", "Attempting to connect to AP: $ssid, Android SDK: ${Build.VERSION.SDK_INT}")
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    connectToApAndroid10Plus(ssid, password)
                } else {
                    connectToApLegacy(ssid, password)
                }
            } catch (e: Exception) {
                Log.e("ApConnectionRepository", "Error connecting to AP", e)
                Result.failure(e)
            }
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun connectToApAndroid10Plus(ssid: String, password: String): Result<String> {
        return withContext(Dispatchers.Main) {
            try {
                Log.d("ApConnectionRepository", "Using Android 10+ connection method")
                
                val specifier = WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build()

                val request = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifier)
                    .build()

                var isConnected = false
                var connectionError: String? = null

                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        Log.d("ApConnectionRepository", "Network available: $ssid")
                        try {
                            connectivityManager.bindProcessToNetwork(network)
                            isConnected = true
                            Log.d("ApConnectionRepository", "Successfully bound to network: $ssid")
                        } catch (e: Exception) {
                            Log.e("ApConnectionRepository", "Failed to bind to network", e)
                            connectionError = "Failed to bind to network: ${e.message}"
                        }
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        Log.e("ApConnectionRepository", "Network unavailable: $ssid")
                        connectionError = "Network unavailable - check password and signal strength"
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        Log.w("ApConnectionRepository", "Lost connection to AP: $ssid")
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        super.onCapabilitiesChanged(network, networkCapabilities)
                        Log.d("ApConnectionRepository", "Network capabilities changed")
                    }
                }

                connectivityManager.requestNetwork(request, networkCallback)

                // Wait for connection with timeout
                val result = withTimeoutOrNull(20000) { // Increased timeout to 20 seconds
                    while (!isConnected && connectionError == null) {
                        delay(500)
                    }
                    isConnected
                }

                if (result == true) {
                    Log.d("ApConnectionRepository", "Connected successfully, verifying connection...")
                    // Wait for network to stabilize
                    delay(2000)
                    
                    // Verify connection by pinging the device
                    val verificationResult = verifyConnection()
                    
                    if (verificationResult.isSuccess) {
                        Log.d("ApConnectionRepository", "Connection verified successfully")
                        verificationResult
                    } else {
                        Log.w("ApConnectionRepository", "Connection established but verification failed")
                        // Still consider it successful if we connected, even if ping fails
                        Result.success("Connected (verification pending)")
                    }
                } else {
                    try {
                        connectivityManager.unregisterNetworkCallback(networkCallback)
                    } catch (e: Exception) {
                        Log.e("ApConnectionRepository", "Error unregistering callback", e)
                    }
                    
                    val errorMessage = connectionError ?: "Connection timeout - network not available within 20 seconds"
                    Log.e("ApConnectionRepository", errorMessage)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Log.e("ApConnectionRepository", "Error in Android 10+ connection", e)
                Result.failure(e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private suspend fun connectToApLegacy(ssid: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ApConnectionRepository", "Using legacy connection method")
                
                // Check if WiFi is enabled
                if (!wifiManager.isWifiEnabled) {
                    Log.e("ApConnectionRepository", "WiFi is not enabled")
                    return@withContext Result.failure(Exception("WiFi is not enabled. Please enable WiFi and try again."))
                }

                // Remove any existing configuration for this SSID
                val existingConfigs = wifiManager.configuredNetworks
                existingConfigs?.forEach { config ->
                    if (config.SSID == "\"$ssid\"") {
                        Log.d("ApConnectionRepository", "Removing existing configuration for $ssid")
                        wifiManager.removeNetwork(config.networkId)
                    }
                }

                val wifiConfig = WifiConfiguration().apply {
                    SSID = "\"$ssid\""
                    preSharedKey = "\"$password\""
                    
                    // Set security type
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                    allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                    allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                    allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                    allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                    allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                    allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                }

                Log.d("ApConnectionRepository", "Adding network configuration for $ssid")
                val networkId = wifiManager.addNetwork(wifiConfig)
                
                if (networkId == -1) {
                    Log.e("ApConnectionRepository", "Failed to add network configuration. This method is deprecated on Android 10+")
                    return@withContext Result.failure(Exception(
                        "Cannot add WiFi network on this Android version. " +
                        "Please connect to the dustbin's WiFi manually through Settings, then try again."
                    ))
                }

                Log.d("ApConnectionRepository", "Network added with ID: $networkId")
                
                wifiManager.disconnect()
                delay(1000)
                
                val enabled = wifiManager.enableNetwork(networkId, true)
                if (!enabled) {
                    Log.e("ApConnectionRepository", "Failed to enable network")
                    return@withContext Result.failure(Exception("Failed to enable network"))
                }
                
                Log.d("ApConnectionRepository", "Network enabled, reconnecting...")
                wifiManager.reconnect()

                // Wait for connection
                delay(5000)

                // Verify connection
                Log.d("ApConnectionRepository", "Verifying connection...")
                verifyConnection()
            } catch (e: Exception) {
                Log.e("ApConnectionRepository", "Error in legacy connection", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun verifyConnection(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ApConnectionRepository", "Attempting to ping device at ${NetworkClient.apiService}")
                val response = NetworkClient.apiService.pingDevice()
                
                if (response.isSuccessful) {
                    val pingResponse = response.body()
                    Log.d("ApConnectionRepository", "Ping successful: ${pingResponse?.status}")
                    Result.success(pingResponse?.status ?: "Connected")
                } else {
                    Log.e("ApConnectionRepository", "Ping failed with code: ${response.code()}")
                    Result.failure(Exception("Ping failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("ApConnectionRepository", "Ping verification failed: ${e.message}", e)
                Result.failure(Exception("Failed to verify connection: ${e.message}"))
            }
        }
    }

    fun disconnectFromAp() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.bindProcessToNetwork(null)
                Log.d("ApConnectionRepository", "Unbound from network")
            }
            Log.d("ApConnectionRepository", "Disconnected from AP")
        } catch (e: Exception) {
            Log.e("ApConnectionRepository", "Error disconnecting from AP", e)
        }
    }
}