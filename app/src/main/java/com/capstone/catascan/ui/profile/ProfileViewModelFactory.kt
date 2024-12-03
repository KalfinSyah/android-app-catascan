package com.capstone.catascan.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.catascan.data.pref.UserPreference

class ProfileViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is ProfileViewModel
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // Safely return the ProfileViewModel, throwing exception if pref is null
            return ProfileViewModel(pref) as T
        }
        // Throw an exception if the ViewModel class is not recognized
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}