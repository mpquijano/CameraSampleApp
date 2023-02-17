package com.mpq.cameratest.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mpq.cameratest.data.datasource.LoginRespository
import com.mpq.cameratest.data.model.User
import com.mpq.cameratest.data.network.response.BaseResponse
import com.mpq.cameratest.data.network.response.LoginResponse
import com.mpq.cameratest.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val loginRespository: LoginRespository)
    : ViewModel()
{

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _session = MutableLiveData<Boolean>(loginRespository.validateToken())
    val session: LiveData<Boolean> = _session

    fun login(username: String, password: String) {
        Log.d(Utils.TAG, "login $username")
        viewModelScope.launch(Dispatchers.IO) {
            loginRespository.login(username, password).enqueue(object:
            Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ){
                    Log.d(Utils.TAG, " LandingViewModel loginRespository login success")
                    if(response.code() == 200){
                        _loginResult.value = LoginResult(success = User(username,password))
                        loggedIn()
                        validateSession()
                    }else{
                        _loginResult.value = LoginResult(error=response.code())
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(Utils.TAG, "LandingViewModel loginRespository login fail ${t.message}")
                    _loginResult.value = LoginResult(error=-1)
                }
            })
        }
    }

    private fun validateSession(){
        Log.d(Utils.TAG, "validateSession")
        _session.value = loginRespository.validateToken()
    }

    private fun loggedIn(){
        Log.d(Utils.TAG, "loggedIn")
        loginRespository.setSessionToken("session")
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}