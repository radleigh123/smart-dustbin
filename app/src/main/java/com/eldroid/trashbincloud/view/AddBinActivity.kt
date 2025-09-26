package com.eldroid.trashbincloud.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
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

        binding = ActivityAddBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val ssid = binding.etWifiSsid.text.toString()
            val password = binding.etWifiPassword.text.toString()
            setWifiCredentials(ssid, password)
        }

        binding.btnPing.setOnClickListener {
            pingDevice()
        }

        binding.btnOpen.setOnClickListener {
            controlServo(90, "manual") // Open position, manual mode
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