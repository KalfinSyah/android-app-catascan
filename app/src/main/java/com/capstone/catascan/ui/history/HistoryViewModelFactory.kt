package com.capstone.catascan.ui.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryViewModelFactory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: HistoryViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): HistoryViewModelFactory {
            if (INSTANCE == null) {
                synchronized(HistoryViewModelFactory::class.java) {
                    INSTANCE = HistoryViewModelFactory(application)
                }
            }
            return INSTANCE as HistoryViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}