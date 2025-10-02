package com.eldroid.trashbincloud.view

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.databinding.ActivityAddBinBinding
import com.eldroid.trashbincloud.retrofit.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddBinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()

        binding = ActivityAddBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val ssid = binding.etWifiSsid.text.toString()
            val password = binding.etWifiPassword.text.toString()
            setWifiCredentials(ssid, password)
        }

        binding.btnPing.setOnClickListener {
            pingDevice()
            scanBins()
        }

        binding.btnOpen.setOnClickListener {
            controlServo(90, "manual") // Open position, manual mode
        }
    }

    private fun checkPermissions() {
        // Check for location permission
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 1)
            return
        }
        
        // Check if location services are enabled
        if (!isLocationEnabled()) {
            showLocationSettingsAlert()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocationSettingsAlert() {
        AlertDialog.Builder(this)
            .setTitle("Location Services Required")
            .setMessage("WiFi scanning requires location services to be enabled. Please enable location services.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Location services are required for WiFi scanning", Toast.LENGTH_LONG).show()
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun scanBins() {
        // Check permissions and location first
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
            checkPermissions()
            return
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show()
            showLocationSettingsAlert()
            return
        }

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Check if WiFi is enabled
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "Please enable WiFi", Toast.LENGTH_SHORT).show()
            return
        }

        val wifiScanReceiver = object: BroadcastReceiver() {
            @SuppressLint("NewApi")
            override fun onReceive(context: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                
                Log.d("SCAN", "Scan completed. Success: $success")
                
                if (success) {
                    val results = wifiManager.scanResults
                    
                    Log.d("SCAN", "Total networks found: ${results.size}")
                    
                    if (results.isEmpty()) {
                        Toast.makeText(this@AddBinActivity, "No WiFi networks detected. Make sure location is ON and WiFi is enabled.", Toast.LENGTH_LONG).show()
                    }
                    
                    results.forEach { result ->
                        Log.d("SCAN", "Network - SSID: '${result.SSID}', BSSID: ${result.BSSID}, Level: ${result.level}")
                    }

                    val dustbins = results.filter {
                        !it.SSID.isNullOrEmpty() && it.SSID.startsWith("DUSTBIN", ignoreCase = true)
                    }

                    Log.d("SCAN", "Found ${dustbins.size} dustbin(s)")
                    dustbins.forEach { result ->
                        Log.d("SCAN", "Dustbin SSID: ${result.SSID}")
                    }

                    Toast.makeText(this@AddBinActivity, "Found ${results.size} networks, ${dustbins.size} dustbin(s)", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("SCAN", "Scan failed")
                    Toast.makeText(this@AddBinActivity, "WiFi scan failed. Try again in a few seconds.", Toast.LENGTH_SHORT).show()
                }
                
                try {
                    unregisterReceiver(this)
                } catch (e: IllegalArgumentException) {
                    Log.e("SCAN", "Receiver already unregistered", e)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        Log.d("SCAN", "Scan started: $success")
        
        if (!success) {
            Toast.makeText(this, "Scan throttled. Wait a few seconds and try again.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Scanning for WiFi networks...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    Toast.makeText(this, "Permission granted. You can now scan for WiFi.", Toast.LENGTH_SHORT).show()
                } else {
                    showLocationSettingsAlert()
                }
            } else {
                Toast.makeText(this, "Location permission denied. Cannot scan WiFi.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // New method for GET requests
    /*private fun makeGetRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkClient.apiService.getSensorData()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.success == true) {
                            apiResponse.data?.let { sensorData ->
                                updateCurrentValue(sensorData.value)
                                Toast.makeText(this@AddBinActivity,
                                    "GET request successful", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@AddBinActivity,
                                "API Error: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AddBinActivity,
                            "HTTP Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddBinActivity,
                        "GET request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HTTP", "GET request failed", e)
                }
            }
        }
    }*/

    // New method for POST requests
    /*private fun makePostRequest() {
        val newValueStr = binding.etNewValue.text.toString()
        if (newValueStr.isBlank()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
            return
        }
        val newValue = newValueStr.toIntOrNull()
        if (newValue == null) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updateRequest = UpdateRequest(newValue, "Android App")
                val response = NetworkClient.apiService.updateSensorData(updateRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse?.success == true) {
                            Toast.makeText(this@AddBinActivity,
                                "POST request successful", Toast.LENGTH_SHORT).show()
                            binding.etNewValue.text.clear()
                            // Optionally refresh data
                            makeGetRequest()
                        } else {
                            Toast.makeText(this@AddBinActivity,
                                "API Error: ${apiResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AddBinActivity,
                            "HTTP Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddBinActivity,
                        "POST request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HTTP", "POST request failed", e)
                }
            }
        }
    }*/

    // TODO: follow MVP
    private fun makeGenericGetRequest(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkClient.apiService.makeGetRequest(url)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("HTTP", "GET Response: $responseBody")
                        Toast.makeText(this@AddBinActivity,
                            "GET request successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AddBinActivity,
                            "GET failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddBinActivity,
                        "GET request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HTTP", "GET request failed", e)
                }
            }
        }
    }

    // TODO: follow MVP
    private fun makeGenericPostRequest(url: String, data: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkClient.apiService.makePostRequest(url, data)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("HTTP", "POST Response: $responseBody")
                        Toast.makeText(this@AddBinActivity,
                            "POST request successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AddBinActivity,
                            "POST failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddBinActivity,
                        "POST request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HTTP", "POST request failed", e)
                }
            }
        }
    }

    private fun pingDevice() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkClient.apiService.pingDevice()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val pingResponse = response.body()
                        Log.d("HTTP", "Ping Response: $pingResponse")
                        binding.tvStatus.text = pingResponse?.let { "Status: ${it.status}" } ?: "Status: Unknown"
                        Toast.makeText(this@AddBinActivity,
                            "Ping successful: ${pingResponse?.status}", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvStatus.text = "Status: Ping Failed"
                        Toast.makeText(this@AddBinActivity,
                            "Ping failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvStatus.text = "Status: Connection Error"
                    Toast.makeText(this@AddBinActivity,
                        "Ping request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HTTP", "Ping request failed", e)
                }
            }
        }
    }

    private fun setWifiCredentials(ssid: String, password: String) {
        if (ssid.isBlank() || password.isBlank()) {
            Toast.makeText(this, "SSID and Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val wifiCredentials = com.eldroid.trashbincloud.retrofit.WifiCredentials(ssid, password)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkClient.apiService.setWifiCredentials(wifiCredentials)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("HTTP", "Set WiFi Response: $responseBody")
                        Toast.makeText(this@AddBinActivity,
                            "WiFi credentials set ${responseBody?.status}", Toast.LENGTH_SHORT).show()

                        binding.etWifiSsid.text.clear()
                        binding.etWifiPassword.text.clear()
                    } else {
                        Toast.makeText(this@AddBinActivity,
                            "Setting WiFi failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddBinActivity,
                        "Set WiFi request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HTTP", "Set WiFi request failed", e)
                }
            }
        }
    }

    private fun controlServo(angle: Int, mode: String) {
        val servoRequest = com.eldroid.trashbincloud.retrofit.ServoRequest(angle, mode)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = NetworkClient.apiService.controlServo(servoRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val servoResponse = response.body()
                        Log.d("HTTP", "Servo Response: $servoResponse")
                        Toast.makeText(this@AddBinActivity,
                            "Servo control: ${servoResponse?.status}", Toast.LENGTH_SHORT).show()
                        
                        // Update UI based on mode
                        if (mode == "manual") {
                            binding.tvStatus.text = "Status: Manual Mode (Angle: $angle°)"
                        } else {
                            binding.tvStatus.text = "Status: Automatic Mode"
                        }
                    } else {
                        Toast.makeText(this@AddBinActivity,
                            "Servo control failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddBinActivity,
                        "Servo request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HTTP", "Servo request failed", e)
                }
            }
        }
    }

    // Helper methods for specific servo actions
    private fun openLidManually() {
        controlServo(90, "manual") // Open position, manual mode
    }

    private fun closeLidManually() {
        controlServo(0, "manual") // Closed position, manual mode
    }

    private fun switchToAutomaticMode() {
        controlServo(0, "auto") // Start in closed position, automatic mode
    }

    private fun getCurrentTime(): String {
        return android.text.format.DateFormat.format("dd/MM/yyyy HH:mm:ss", Date()).toString()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}