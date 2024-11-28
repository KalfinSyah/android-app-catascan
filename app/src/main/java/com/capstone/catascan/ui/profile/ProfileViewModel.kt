package com.capstone.catascan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.catascan.data.pref.SettingPreferences
import kotlinx.coroutines.launch

class ProfileViewModel(private val pref: SettingPreferences): ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getDailyReminderSetting(): LiveData<Boolean> {
        return pref.getDailyReminderSetting().asLiveData()
    }

    fun saveDailyReminderSetting(isDailyReminderActive: Boolean) {
        viewModelScope.launch {
            pref.saveDailyReminderSetting(isDailyReminderActive)
        }
    }
}