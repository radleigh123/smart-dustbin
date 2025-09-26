package com.eldroid.trashbincloud.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @GET("api/sensor/data")
    suspend fun getSensorData(): Response<ApiResponse<SensorData>>

    @POST
    suspend fun updateSensorData(@Body request: UpdateRequest): Response<ApiResponse<SensorData>>

    // Generic GET request
    suspend fun makeGetRequest(@Url url: String): Response<String>

    // Generic POST request
    @POST
    suspend fun makePostRequest(
        @Url url: String,
        @Body body: Any
    ): Response<String>

    @GET("ping")
    suspend fun pingDevice(): Response<PingResponse>

    @POST("setwifi")
    suspend fun setWifiCredentials(@Body wifiCredentials: WifiCredentials): Response<WifiSetupResponse>

    @POST("servo")
    suspend fun controlServo(@Body servoRequest: ServoRequest): Response<ServoResponse>
}