package com.capstone.catascan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CataractType(
    val name: String,
    val description: String,
    val photo: Int
) : Parcelable
