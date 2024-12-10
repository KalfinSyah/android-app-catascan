package com.capstone.catascan.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.api.ApiConfig
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.response.LoginResponse
import com.capstone.catascan.data.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _popupMessage = MutableLiveData<String>().apply { value = "" }
    val popupMessage: LiveData<String> = _popupMessage

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    val initiateAutoLogin = MutableLiveData<Boolean>().apply { value = false }
    val agreeTerms = MutableLiveData<Boolean>().apply { value = false }

    val registerButtonEnabler = MutableLiveData(mutableListOf(false, false, false, false))



    suspend fun saveSession(user: UserModel) {
        pref.saveSession(user)
    }


    fun register(registerData: Map<String, String>) {
        _isLoading.value = true

        val client = ApiConfig.getApiServiceForAuth().register(registerData)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    login(
                        mapOf(
                            "email" to registerData["email"]!!,
                            "password" to registerData["password"]!!
                        )
                    )
                } else {
                    _popupMessage.value = "${response.message()}\nPossibility: email already exists, invalid email format, password contains less than 8 char, server error"
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _popupMessage.value = "Failed to register, check your connection!"
                _isLoading.value = false
                initiateAutoLogin.value = false
            }
        })
    }

    fun login(loginData: Map<String, String>) {
        _isLoading.value = true

        val client = ApiConfig.getApiServiceForAuth().login(loginData)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _popupMessage.value = "Register Success"
                    _loginResult.value = response.body()
                } else {
                    println("${response.code()}, ${response.message()}")
                    _popupMessage.value = "Error Auto Login! Probably you input the wrong email or password,  or the server error."
                    initiateAutoLogin.value = false
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _popupMessage.value = "Failed to auto login, check your connection!"
                _isLoading.value = false
            }
        })
    }
}