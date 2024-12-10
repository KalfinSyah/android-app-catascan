package com.capstone.catascan.data.response

import com.google.gson.annotations.SerializedName

data class ListHistoryResponse(

	@field:SerializedName("data")
	val data: List<ItemHistory>,

	@field:SerializedName("message")
	val message: String
)

data class ItemHistory(

	@field:SerializedName("result")
	val result: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("userId")
	val userId: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
