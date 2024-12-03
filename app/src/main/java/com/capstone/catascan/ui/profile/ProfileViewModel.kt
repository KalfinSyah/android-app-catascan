package com.capstone.catascan.ui.profile

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.pref.UserPreference
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProfileViewModel(private val pref: UserPreference) : ViewModel() {

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
                // Optionally log or handle the start of loading
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
                emit(UserModel("Unknown", "unknown@example.com", "", false)) // Default user data if error occurs
            }
            .onStart {
                // Optionally log or handle the start of loading
                Log.d("ProfileViewModel", "Fetching user session...")
            }
            .asLiveData()
    }
}
