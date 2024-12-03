package com.capstone.catascan.ui.scan.preview

import android.app.Application
import android.net.Uri
import android.text.Spanned
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.catascan.repository.HistoryRepository
import com.capstone.catascan.data.history.History

class PreviewViewModel(application: Application): ViewModel() {
    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)
    val setCapturedImage = MutableLiveData<Uri>()
    val setResultVisibility = MutableLiveData<Boolean>()
    val fromRecent = MutableLiveData<String>()
    val result = MutableLiveData<Spanned>()

    fun saveToHistory(timeStamps: String, image: String, result: String) {
        mHistoryRepository.insert(
            History(
                timeStamp = timeStamps,
                image = image,
                result = result
            )
        )
    }
}