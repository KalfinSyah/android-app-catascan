package com.capstone.catascan.ui.scan.preview

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PreviewViewModelFactory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: PreviewViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): PreviewViewModelFactory {
            if (INSTANCE == null) {
                synchronized(PreviewViewModelFactory::class.java) {
                    INSTANCE = PreviewViewModelFactory(application)
                }
            }
            return INSTANCE as PreviewViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreviewViewModel::class.java)) {
            return PreviewViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}