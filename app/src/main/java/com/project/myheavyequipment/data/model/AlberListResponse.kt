package com.project.myheavyequipment.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AlberListResponse(

	@field:SerializedName("payload")
	val payload: List<PayloadItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
data class PayloadItem(

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("capacity")
	val capacity: String? = null,

	@field:SerializedName("lifting_height")
	val liftingHeight: String? = null,

	@field:SerializedName("stage")
	val stage: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("engine")
	val engine: String? = null,

	@field:SerializedName("load_center")
	val loadCenter: String? = null,

	@field:SerializedName("jenis")
	val jenis: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("unique_code")
	val uniqueCode: String? = null,

	@field:SerializedName("hours_meter")
	val hoursMeter: String? = null,

	@field:SerializedName("latest_status")
	val latestStatus: Int? = null
) : Parcelable
