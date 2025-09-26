package com.eldroid.trashbincloud.retrofit

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class SensorData(
    val id: String,
    val value: Int,
    val timestamp: Long,
    val deviceId: String
)

data class UpdateRequest(
    val value: Int,
    val source: String = "Android App"
)

data class PingResponse(
    val status: String,
    val message: String? = null,
    val timestamp: Long? = null
)

data class WifiCredentials(
    val ssid: String,
    val password: String
)

data class WifiSetupResponse(
    val status: String,
    val message: String? = null,
    val success: Boolean? = null
)

data class ServoRequest(
    val angle: Int,
    val mode: String
)

data class ServoResponse(
    val status: String,
    val message: String? = null
)