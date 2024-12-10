package com.capstone.catascan.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.catascan.R
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.api.ApiConfig
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.response.ArticlesItem
import com.capstone.catascan.data.response.GetUserResponse
import com.capstone.catascan.data.response.NewsResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val pref: UserPreference, private  val application: Application) : ViewModel()  {
    private val _articlesItem = MutableLiveData<List<ArticlesItem?>?>()
    val articlesItem: LiveData<List<ArticlesItem?>?> = _articlesItem

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    val errMsg = MutableLiveData<String>().apply { value = "" }

    private val _getUserResult = MutableLiveData<GetUserResponse>()
    val getUserResult: LiveData<GetUserResponse> = _getUserResult

    init {
        findNews()
    }

    fun retry() {
        errMsg.value = ""
        _articlesItem.value = emptyList()
        findNews()
    }

    fun getUserSession(): LiveData<UserModel> {
        return pref.getSession()
            .catch { e ->
                // Log the error and emit a default value
                Log.e("ProfileViewModel", "Error fetching user session", e)
            }
            .onStart {
                // Optionally log or handle the start of loading
                Log.d("ProfileViewModel", "Fetching user session...")
            }
            .asLiveData()
    }

    fun getUserData(token: String) {
        val client = ApiConfig.getApiServiceForAuth().getUser("Bearer $token")
        client.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>) {
                if (response.isSuccessful) {
                    _getUserResult.value = response.body()
                } else {
                    Log.d("HomeViewModel", response.message())
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.d("HomeViewModel", "from onFailure: ${t.message}")
            }
        })
    }

    private fun findNews() {
        _isLoading.value = true
        val client = ApiConfig.getApiServiceForNews().getNews()
        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _articlesItem.value = response.body()?.articles
                } else {
                    errMsg.value = application.applicationContext.getString(R.string.error_msg)
                }
            }

            override fun onFailure(call: Call<NewsResponse?>, t: Throwable) {
                _isLoading.value = false
                errMsg.value = application.applicationContext.getString(R.string.error_msg)
            }
        })
    }
}