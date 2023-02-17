package com.mpq.cameratest.data.datasource

import android.util.Log
import com.mpq.cameratest.data.network.request.LoginRequest
import com.mpq.cameratest.data.network.response.BaseResponse
import com.mpq.cameratest.data.network.response.LoginResponse
import com.mpq.cameratest.network.SessionManager
import com.mpq.cameratest.utils.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoginRespository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
    ){

    suspend fun login(username: String,password: String): Call<Void>{
        Log.d(Utils.TAG, "LoginRespository login")
        return apiService.login()
    }

    fun validateToken(): Boolean{
        return sessionManager.getToken() != null
    }

    fun setSessionToken(token: String){
        sessionManager.saveAuthToken(token)
    }
}