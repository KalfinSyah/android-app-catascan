package com.capstone.catascan.data.response

import com.google.gson.annotations.SerializedName

data class UploadFotoProfileResponse (
        @field:SerializedName("message")
        val message: String,

        @field:SerializedName("profileImageUrl")
        val profileImageUrl: String
)