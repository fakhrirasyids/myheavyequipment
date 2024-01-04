package com.project.myheavyequipment.data.model

import com.google.gson.annotations.SerializedName

data class ScanResponse(

	@field:SerializedName("payload")
	val payload: List<Any?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
