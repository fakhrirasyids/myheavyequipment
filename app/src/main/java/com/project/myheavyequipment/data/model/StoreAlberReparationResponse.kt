package com.project.myheavyequipment.data.model

import com.google.gson.annotations.SerializedName

data class StoreAlberReparationResponse(

	@field:SerializedName("payload")
	val payload: PayloadStore? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class PayloadStore(

	@field:SerializedName("note")
	val note: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("item_id")
	val itemId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("hours_meter")
	val hoursMeter: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
