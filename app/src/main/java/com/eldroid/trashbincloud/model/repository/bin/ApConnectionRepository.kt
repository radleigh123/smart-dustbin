package com.eldroid.trashbincloud.model.repository.bin

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

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

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
                Log.d("ApConnectionRepository", "Using Android 10+ connection method for SSID: $ssid")
                
                // Unregister any existing callback first
                networkCallback?.let {
                    try {
                        connectivityManager.unregisterNetworkCallback(it)
                        Log.d("ApConnectionRepository", "Unregistered previous network callback")
                    } catch (e: Exception) {
                        Log.w("ApConnectionRepository", "No previous callback to unregister")
                    }
                }

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
                var connectedNetwork: Network? = null

                networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        Log.d("ApConnectionRepository", "Network available: $ssid")
                        
                        try {
                            // Bind the process to this network
                            val bindResult = connectivityManager.bindProcessToNetwork(network)
                            if (bindResult) {
                                connectedNetwork = network
                                isConnected = true
                                Log.d("ApConnectionRepository", "Successfully bound to network: $ssid")
                            } else {
                                connectionError = "Failed to bind to network"
                                Log.e("ApConnectionRepository", "Failed to bind to network")
                            }
                        } catch (e: Exception) {
                            connectionError = "Failed to bind to network: ${e.message}"
                            Log.e("ApConnectionRepository", "Failed to bind to network", e)
                        }
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        Log.e("ApConnectionRepository", "Network unavailable: $ssid")
                        connectionError = "Network unavailable. Check password and try again."
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        Log.w("ApConnectionRepository", "Lost connection to: $ssid")
                        if (!isConnected) {
                            connectionError = "Connection lost"
                        }
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        super.onCapabilitiesChanged(network, networkCapabilities)
                        val hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        Log.d("ApConnectionRepository", "Capabilities changed - Has WiFi: $hasWifi")
                    }
                }

                // Request network connection - this will show system dialog on Android 10+
                connectivityManager.requestNetwork(request, networkCallback!!)
                Log.d("ApConnectionRepository", "Network request submitted. System dialog should appear...")

                // Wait for connection with timeout
                val result = withTimeoutOrNull(30000) { // 30 seconds timeout
                    while (!isConnected && connectionError == null) {
                        delay(500)
                    }
                    isConnected
                }

                if (result == true && connectedNetwork != null) {
                    Log.d("ApConnectionRepository", "Connected successfully, verifying connection...")
                    
                    // Give the network time to stabilize
                    delay(2000)
                    
                    // Verify connection by pinging the device
                    val verificationResult = verifyConnection()
                    
                    if (verificationResult.isSuccess) {
                        Log.d("ApConnectionRepository", "Connection verified successfully")
                        Result.success("Connected and verified")
                    } else {
                        Log.w("ApConnectionRepository", "Connection established but verification failed: ${verificationResult.exceptionOrNull()?.message}")
                        // Still return success if connected, ping might fail for other reasons
                        Result.success("Connected (awaiting verification)")
                    }
                } else {
                    // Cleanup on failure
                    networkCallback?.let {
                        try {
                            connectivityManager.unregisterNetworkCallback(it)
                        } catch (e: Exception) {
                            Log.e("ApConnectionRepository", "Error unregistering callback", e)
                        }
                    }
                    networkCallback = null
                    
                    val errorMessage = when {
                        connectionError != null -> connectionError!!
                        result == null -> "Connection timeout. Make sure you accepted the system dialog and the password is correct."
                        else -> "Failed to connect"
                    }
                    
                    Log.e("ApConnectionRepository", "Connection failed: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Log.e("ApConnectionRepository", "Error in Android 10+ connection", e)
                
                // Cleanup on exception
                networkCallback?.let {
                    try {
                        connectivityManager.unregisterNetworkCallback(it)
                    } catch (cleanupException: Exception) {
                        Log.e("ApConnectionRepository", "Error during cleanup", cleanupException)
                    }
                }
                networkCallback = null
                
                Result.failure(e)
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun connectToApLegacy(ssid: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ApConnectionRepository", "Using legacy connection method for SSID: $ssid")
                
                // Check if WiFi is enabled
                if (!wifiManager.isWifiEnabled) {
                    Log.e("ApConnectionRepository", "WiFi is not enabled")
                    return@withContext Result.failure(Exception("Please enable WiFi and try again"))
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
                    
                    // Set WPA/WPA2 security
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
                    Log.e("ApConnectionRepository", "Failed to add network - addNetwork returned -1")
                    return@withContext Result.failure(Exception("Failed to add network configuration. Please try connecting manually through WiFi settings."))
                }

                Log.d("ApConnectionRepository", "Network added with ID: $networkId, enabling...")
                
                wifiManager.disconnect()
                delay(1000)
                
                val enabled = wifiManager.enableNetwork(networkId, true)
                if (!enabled) {
                    Log.e("ApConnectionRepository", "Failed to enable network")
                    wifiManager.removeNetwork(networkId)
                    return@withContext Result.failure(Exception("Failed to enable network"))
                }
                
                Log.d("ApConnectionRepository", "Network enabled, reconnecting...")
                wifiManager.reconnect()

                // Wait for connection
                delay(5000)

                // Verify connection
                Log.d("ApConnectionRepository", "Verifying connection...")
                val verificationResult = verifyConnection()
                
                if (verificationResult.isSuccess) {
                    Result.success("Connected and verified")
                } else {
                    Log.w("ApConnectionRepository", "Connected but verification failed")
                    Result.success("Connected (awaiting verification)")
                }
            } catch (e: Exception) {
                Log.e("ApConnectionRepository", "Error in legacy connection", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun verifyConnection(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ApConnectionRepository", "Pinging device at http://192.168.4.1/")
                val response = NetworkClient.apiService.pingDevice()
                
                if (response.isSuccessful) {
                    val pingResponse = response.body()
                    Log.d("ApConnectionRepository", "Ping successful: ${pingResponse?.status}")
                    Result.success(pingResponse?.status ?: "Connected")
                } else {
                    Log.e("ApConnectionRepository", "Ping failed with HTTP code: ${response.code()}")
                    Result.failure(Exception("Device not responding (HTTP ${response.code()})"))
                }
            } catch (e: Exception) {
                Log.e("ApConnectionRepository", "Ping verification failed: ${e.message}", e)
                Result.failure(Exception("Cannot reach device: ${e.message}"))
            }
        }
    }

    fun disconnectFromAp() {
        try {
            // Unregister callback if exists
            networkCallback?.let {
                try {
                    connectivityManager.unregisterNetworkCallback(it)
                    Log.d("ApConnectionRepository", "Unregistered network callback")
                } catch (e: Exception) {
                    Log.w("ApConnectionRepository", "Callback already unregistered", e)
                }
            }
            networkCallback = null
            
            // Unbind from network
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