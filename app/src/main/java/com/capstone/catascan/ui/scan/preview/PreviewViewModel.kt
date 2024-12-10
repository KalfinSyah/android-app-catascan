package com.capstone.catascan.ui.scan.preview

import android.app.Application
import android.net.Uri
import android.text.Spanned
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.api.ApiConfig
import com.capstone.catascan.data.history.History
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.response.HistoryResponse
import com.capstone.catascan.repository.HistoryRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PreviewViewModel(private val pref: UserPreference, application: Application): ViewModel() {
    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)
    val setCapturedImage = MutableLiveData<Uri>()
    val setResultVisibility = MutableLiveData<Boolean>()
    val fromRecent = MutableLiveData<String>()
    val result = MutableLiveData<Spanned>()
    val dataHistory = MutableLiveData<MutableList<String>>(mutableListOf())
    var tokenBuffer = MutableLiveData<String?>().apply { value = null }

    val popupMessage = MutableLiveData<String>().apply { value = "" }

    private val _loading = MutableLiveData<Boolean>().apply { value = false }
    val loading: LiveData<Boolean> = _loading

    fun saveToHistory(image: String, result: String) {
        mHistoryRepository.insert(
            History(
                image = image,
                result = result
            )
        )
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

    fun uploadHistory(historyImage: File, result: String, token: String) {
        _loading.value = true

        // Prepare the file as MultipartBody.Part
        val requestFile = historyImage.asRequestBody("image/jpeg".toMediaType())
        val body = MultipartBody.Part.createFormData("file", historyImage.name, requestFile)

        // Prepare the text as RequestBody
        val resultRequestBody = result.toRequestBody("text/plain".toMediaType())

        val client = ApiConfig.getApiServiceForAuth().uploadHistory(body, resultRequestBody, "Bearer $token")
        client.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                _loading.value = false
                if (response.isSuccessful) {
                    popupMessage.value = "History Uploaded"
                } else {
                    Log.d("PreviewViewModelLog", response.message())
                    popupMessage.value = "error when tried to upload history"
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                _loading.value = false
                Log.d("PreviewViewModelLog", "from onFailure: ${t.message}")
                popupMessage.value = "error when tried to upload history, check your connection!"
            }
        })
    }
}