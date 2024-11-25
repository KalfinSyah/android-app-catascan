package com.capstone.catascan.ui.scan.preview

import android.net.Uri
import android.text.SpannableString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PreviewViewModel: ViewModel() {
    val setCapturedImage = MutableLiveData<Uri>()
    val setResultVisibility = MutableLiveData<Boolean>()
    val result = MutableLiveData<SpannableString>()
}