package com.capstone.catascan.ui.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.api.ApiConfig
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.response.GetUserResponse
import com.capstone.catascan.data.response.UploadFotoProfileResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileViewModel(private val pref: UserPreference) : ViewModel() {

    val popupMessage = MutableLiveData<String>().apply { value = "" }
    var uriBuffer = MutableLiveData<Uri?>().apply { value = null }
    var tokenBuffer = MutableLiveData<String?>().apply { value = null }
    var showProgressBar = MutableLiveData<Boolean>().apply { value = false }
    val getUserResult = MutableLiveData<GetUserResponse>()


    // Save settings with proper error handling and logging
    fun saveLanguageSetting(language: String) {
        viewModelScope.launch {
            try {
                pref.saveLanguageSetting(language)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving language setting", e)
            }
        }
    }

    fun saveDailyReminderSetting(isDailyReminderActive: Boolean) {
        viewModelScope.launch {
            try {
                pref.saveDailyReminderSetting(isDailyReminderActive)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving daily reminder setting", e)
            }
        }
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            try {
                pref.saveThemeSetting(isDarkModeActive)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving theme setting", e)
            }
        }
    }

    // Get settings with error handling, logging, and default values to avoid crashes
    fun getLanguageSetting(): LiveData<String> {
        return pref.getLanguageSetting()
            .catch { e ->
                // Log the error and emit a default value
                Log.e("ProfileViewModel", "Error fetching language setting", e)
                emit("English") // Default language if error occurs
            }
            .onStart {
                Log.d("ProfileViewModel", "Fetching language setting...")
            }
            .asLiveData()
    }

    fun getDailyReminderSetting(): LiveData<Boolean> {
        return pref.getDailyReminderSetting()
            .catch { e ->
                // Log the error and emit a default value
                Log.e("ProfileViewModel", "Error fetching daily reminder setting", e)
                emit(false) // Default to "off" if error occurs
            }
            .onStart {
                // Optionally log or handle the start of loading
                Log.d("ProfileViewModel", "Fetching daily reminder setting...")
            }
            .asLiveData()
    }

    fun getThemeSetting(): LiveData<Boolean> {
        return pref.getThemeSetting()
            .catch { e ->
                // Log the error and emit a default value
                Log.e("ProfileViewModel", "Error fetching theme setting", e)
                emit(false) // Default to light theme if error occurs
            }
            .onStart {
                // Optionally log or handle the start of loading
                Log.d("ProfileViewModel", "Fetching theme setting...")
            }
            .asLiveData()
    }

    // Logout function to clear user session
    fun logout(context: Context, application: Application) {
        viewModelScope.launch {
            try {
                pref.logout(context, application) // Clear session data
                Log.d("ProfileViewModel", "User logged out successfully")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error logging out", e)
            }
        }
    }

    // Get the user session data (name, email)
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

    fun uploadProfile(token: String, imageFile: File) {
        showProgressBar.value = true
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody : MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",  // just like the key in postman
            imageFile.name,
            requestImageFile
        )
        val client = ApiConfig.getApiServiceForAuth().uploadProfileImage(multipartBody, "Bearer $token")
        client.enqueue(object : Callback<UploadFotoProfileResponse> {
            override fun onResponse(
                call: Call<UploadFotoProfileResponse>,
                response: Response<UploadFotoProfileResponse>
            ) {
                if (response.isSuccessful) {
                    popupMessage.value = "Change Profile Success"
                } else {
                    popupMessage.value = "error when tried to change profile"
                }
                showProgressBar.value = false
            }

            override fun onFailure(call: Call<UploadFotoProfileResponse>, t: Throwable) {
                popupMessage.value = "error when tried to change profile, check your connection!"
                showProgressBar.value = false
            }
        })
    }

    fun getUserData(token: String) {
        val client = ApiConfig.getApiServiceForAuth().getUser("Bearer $token")
        client.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>) {
                if (response.isSuccessful) {
                    getUserResult.value = response.body()
                } else {
                    Log.d("HomeViewModel", response.message())
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.d("HomeViewModel", "from onFailure: ${t.message}")
            }
        })
    }
}
