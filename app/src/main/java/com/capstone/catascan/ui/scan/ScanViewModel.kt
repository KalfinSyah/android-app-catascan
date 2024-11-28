package com.capstone.catascan.ui.scan

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.catascan.HistoryRepository
import com.capstone.catascan.data.history.History

class ScanViewModel(application: Application) : ViewModel() {
    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)

    fun getAllHistory(): LiveData<List<History>> = mHistoryRepository.getAllHistory()
}