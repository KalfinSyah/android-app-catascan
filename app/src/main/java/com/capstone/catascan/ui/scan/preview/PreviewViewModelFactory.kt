package com.capstone.catascan.ui.scan.preview

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.catascan.data.pref.UserPreference

class PreviewViewModelFactory(private val pref: UserPreference, private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreviewViewModel::class.java)) {
            return PreviewViewModel(pref, mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}