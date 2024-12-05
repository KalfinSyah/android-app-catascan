package com.capstone.catascan.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.api.ApiConfig
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.response.LoginResponse
import com.capstone.catascan.data.response.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _popupMessage = MutableLiveData<String>().apply { value = "" }
    val popupMessage: LiveData<String> = _popupMessage

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    suspend fun saveSession(user: UserModel) {
        pref.saveSession(user)
    }

    fun login(email: String, password: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiServiceForAuth().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _popupMessage.value = "Login Success"
                    _loginResult.value = response.body()?.loginResult
                } else {
                    println("${response.code()}, ${response.message()}")
                    _popupMessage.value = "Error Login! Probably you input the wrong wrong email or password,  or the server error."
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _popupMessage.value = "Failed to login, check your connection!"
                _isLoading.value = false
            }
        })
    }
}