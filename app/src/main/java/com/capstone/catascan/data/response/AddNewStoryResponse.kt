package com.capstone.catascan.data.response

import com.google.gson.annotations.SerializedName

class AddNewStoryResponse (
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)