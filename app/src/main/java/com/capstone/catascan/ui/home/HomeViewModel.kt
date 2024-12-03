package com.capstone.catascan.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.catascan.R
import com.capstone.catascan.data.api.ApiConfig
import com.capstone.catascan.data.api.ArticlesItem
import com.capstone.catascan.data.api.NewsResponse
import retrofit2.Call

class HomeViewModel(private  val application: Application) : ViewModel()  {
    private val _articlesItem = MutableLiveData<List<ArticlesItem?>?>()
    val articlesItem: LiveData<List<ArticlesItem?>?> = _articlesItem

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    val errMsg = MutableLiveData<String>().apply { value = "" }


    init {
        findNews()
    }

    fun retry() {
        errMsg.value = ""
        _articlesItem.value = emptyList()
        findNews()
    }

    private fun findNews() {
        _isLoading.value = true
        val client = ApiConfig.getApiServiceForNews().getNews()
        client.enqueue(object : retrofit2.Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: retrofit2.Response<NewsResponse>
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