package com.capstone.catascan.data.response

import com.google.gson.annotations.SerializedName

data class GetUserResponse(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("profileImageUrl")
	val profileImageUrl: String = "https://picsum.photos/200",

	@field:SerializedName("email")
	val email: String
)
