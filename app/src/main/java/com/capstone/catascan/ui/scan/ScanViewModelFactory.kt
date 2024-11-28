package com.capstone.catascan.ui.scan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScanViewModelFactory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ScanViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): ScanViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ScanViewModelFactory::class.java) {
                    INSTANCE = ScanViewModelFactory(application)
                }
            }
            return INSTANCE as ScanViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            return ScanViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}