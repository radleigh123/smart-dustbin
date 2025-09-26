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
