package com.eldroid.trashbincloud.model.repository.bin

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.eldroid.trashbincloud.model.entity.bin.FoundBin
import java.util.*

class BleProvisioningManager(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "BleProvisioningManager"

        // Update these UUIDs to match your ESP32 BLE provisioning service
        private const val PROVISIONING_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
        private const val WIFI_SSID_CHAR_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"
        private const val WIFI_PASSWORD_CHAR_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
        private const val WIFI_STATUS_CHAR_UUID = "6e400004-b5a3-f393-e0a9-e50e24dcca9e"
        private const val WIFI_SCAN_CHAR_UUID = "6e400005-b5a3-f393-e0a9-e50e24dcca9e"
    }
    
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    
    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    
    private var bluetoothGatt: BluetoothGatt? = null
    private var onScanResult: ((FoundBin) -> Unit)? = null
    private var onConnectionStateChanged: ((Boolean) -> Unit)? = null
    private var onProvisioningComplete: ((Boolean, String?) -> Unit)? = null
    private var onWifiNetworksReceived: ((List<String>) -> Unit)? = null
    private var onServicesReady: (() -> Unit)? = null  // ADD THIS
    
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceName = device.name ?: "Unknown"
            
            // Filter for your dustbin devices (adjust prefix as needed)
            if (deviceName.startsWith("SmartDustbin") || deviceName.contains("TrashBin")) {
                val bin = FoundBin(
                    binId = device.address,
                    name = deviceName,
                    location = "Signal: ${result.rssi} dBm",
                    isProvisioned = false
                )
                onScanResult?.invoke(bin)
                Log.d(TAG, "Found device: $deviceName (${device.address})")
            }
        }
        
        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE Scan failed with error: $errorCode")
        }
    }
    
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server")
                    onConnectionStateChanged?.invoke(true)
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server")
                    onConnectionStateChanged?.invoke(false)
                }
            }
        }
        
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services discovered successfully")
                
                // Log all discovered services and characteristics
                gatt.services?.forEach { service ->
                    Log.d(TAG, "Service UUID: ${service.uuid}")
                    service.characteristics?.forEach { characteristic ->
                        Log.d(TAG, "  - Characteristic UUID: ${characteristic.uuid}")
                    }
                }
                
                // Check if our provisioning service exists
                val provService = gatt.getService(UUID.fromString(PROVISIONING_SERVICE_UUID))
                if (provService != null) {
                    Log.d(TAG, "✓ Provisioning service found!")
                    // NOTIFY THAT SERVICES ARE READY
                    onServicesReady?.invoke()
                } else {
                    Log.e(TAG, "✗ Provisioning service NOT found")
                    Log.e(TAG, "Expected UUID: $PROVISIONING_SERVICE_UUID")
                }
            } else {
                Log.e(TAG, "Service discovery failed with status: $status")
            }
        }
        
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.uuid == UUID.fromString(WIFI_SCAN_CHAR_UUID)) {
                    // Parse WiFi networks from characteristic
                    val networksString = String(characteristic.value)
                    val networks = networksString.split(",").filter { it.isNotBlank() }
                    Log.d(TAG, "Received WiFi networks: $networks")
                    onWifiNetworksReceived?.invoke(networks)
                }
            }
        }
        
        @SuppressLint("MissingPermission")
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic written successfully: ${characteristic.uuid}")
                
                // Check if this was the SSID write
                if (characteristic.uuid == UUID.fromString(WIFI_SSID_CHAR_UUID)) {
                    Log.d(TAG, "SSID written successfully, now writing password...")
                    
                    // Now write the password
                    val password = pendingPasswordWrite
                    val passwordChar = pendingPasswordChar
                    
                    if (password != null && passwordChar != null) {
                        passwordChar.value = password.toByteArray()
                        val success = gatt.writeCharacteristic(passwordChar)
                        
                        if (success) {
                            Log.d(TAG, "Writing password (hidden for security)")
                        } else {
                            Log.e(TAG, "Failed to initiate password write")
                            onProvisioningComplete?.invoke(false, "Failed to write password")
                        }
                        
                        // Clear pending data
                        pendingPasswordWrite = null
                        pendingPasswordChar = null
                    }
                }
                // Check if this was the password write
                else if (characteristic.uuid == UUID.fromString(WIFI_PASSWORD_CHAR_UUID)) {
                    Log.d(TAG, "Password written successfully!")
                    Log.d(TAG, "WiFi credentials sent completely via BLE")
                    
                    // Provisioning complete - notify success
                    onProvisioningComplete?.invoke(true, null)
                }
            } else {
                Log.e(TAG, "Characteristic write failed with status: $status for ${characteristic.uuid}")
                onProvisioningComplete?.invoke(false, "Write failed with status: $status")
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    fun startScanning(onResult: (FoundBin) -> Unit) {
        onScanResult = onResult
        
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        
        bluetoothLeScanner?.startScan(null, scanSettings, scanCallback)
        Log.d(TAG, "BLE scanning started")
    }
    
    @SuppressLint("MissingPermission")
    fun stopScanning() {
        bluetoothLeScanner?.stopScan(scanCallback)
        Log.d(TAG, "BLE scanning stopped")
    }
    
    @SuppressLint("MissingPermission")
    fun connectToDevice(
        deviceAddress: String,
        onConnectionChanged: (Boolean) -> Unit,
        onReady: () -> Unit  // ADD THIS PARAMETER
    ) {
        onConnectionStateChanged = onConnectionChanged
        onServicesReady = onReady  // SAVE THE CALLBACK
        
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        bluetoothGatt = device?.connectGatt(context, false, gattCallback)
    }
    
    @SuppressLint("MissingPermission")
    fun provisionWifi(
        ssid: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        onProvisioningComplete = onComplete
        
        val service = bluetoothGatt?.getService(UUID.fromString(PROVISIONING_SERVICE_UUID))
        
        if (service == null) {
            onComplete(false, "Provisioning service not found")
            return
        }
        
        // Get characteristics
        val ssidChar = service.getCharacteristic(UUID.fromString(WIFI_SSID_CHAR_UUID))
        val passwordChar = service.getCharacteristic(UUID.fromString(WIFI_PASSWORD_CHAR_UUID))
        
        if (ssidChar == null || passwordChar == null) {
            onComplete(false, "Required characteristics not found")
            return
        }
        
        // Store password to write after SSID write completes
        pendingPasswordWrite = password
        pendingPasswordChar = passwordChar
        
        // Write SSID first
        ssidChar.value = ssid.toByteArray()
        val success = bluetoothGatt?.writeCharacteristic(ssidChar)
        
        if (success == true) {
            Log.d(TAG, "Writing SSID: $ssid")
        } else {
            onComplete(false, "Failed to initiate SSID write")
        }
    }
    
    // Add these class properties at the top of the class
    private var pendingPasswordWrite: String? = null
    private var pendingPasswordChar: BluetoothGattCharacteristic? = null
    
    @SuppressLint("MissingPermission")
    fun requestWifiNetworks(onNetworks: (List<String>) -> Unit) {
        onWifiNetworksReceived = onNetworks
        
        val service = bluetoothGatt?.getService(UUID.fromString(PROVISIONING_SERVICE_UUID))
        if (service == null) {
            Log.e(TAG, "Provisioning service not found")
            onNetworks(emptyList())
            return
        }
        
        val scanChar = service.getCharacteristic(UUID.fromString(WIFI_SCAN_CHAR_UUID))
        if (scanChar == null) {
            Log.e(TAG, "WiFi scan characteristic not found")
            onNetworks(emptyList())
            return
        }
        
        // Read the characteristic to trigger WiFi scan on ESP32
        bluetoothGatt?.readCharacteristic(scanChar)
        Log.d(TAG, "Requesting WiFi networks from device")
    }
    
    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
    
    fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true
}