package com.mpq.cameratest.data.datasource

import com.mpq.cameratest.data.network.request.LoginRequest
import com.mpq.cameratest.data.network.response.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    /*
    200 @POST("6026b35a-6d11-4f8d-ab1c-f5621982784b")
    400 @POST("cacf7a00-a6d8-489f-a293-f8205587450f")
    404 @POST("6b640229-8c3f-4e0a-a3f1-8215c17209c9")
    502 @POST("22c4c3dd-d3e4-4a0b-b1ee-6f39b4b1ca42")
     */
    @POST("6026b35a-6d11-4f8d-ab1c-f5621982784b")
    fun login() : Call<Void>
}