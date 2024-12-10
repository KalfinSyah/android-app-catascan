package com.capstone.catascan.ui.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.api.ApiConfig
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.response.ItemHistory
import com.capstone.catascan.data.response.ListHistoryResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import retrofit2.Call

class ScanViewModel(private val pref: UserPreference) : ViewModel() {
    private val _historyItem = MutableLiveData<List<ItemHistory>>()
    val historyItem: LiveData<List<ItemHistory>> = _historyItem

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

    fun getAllCloudHistory(token: String) {
        val client = ApiConfig.getApiServiceForAuth().getAllHistory("Bearer $token")
        client.enqueue(object : retrofit2.Callback<ListHistoryResponse> {
            override fun onResponse(
                call: Call<ListHistoryResponse>,
                response: retrofit2.Response<ListHistoryResponse>
            ) {
                if (response.isSuccessful) {
                    _historyItem.value = response.body()?.data
                } else {
                    Log.d("ScanViewModelLog", response.message())
                }
            }

            override fun onFailure(call: Call<ListHistoryResponse?>, t: Throwable) {
                Log.d("ScanViewModelLog", "OnFailure: ${t.message}")
            }
        })
    }
}