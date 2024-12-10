package com.capstone.catascan.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("email")
	val email: String
)
