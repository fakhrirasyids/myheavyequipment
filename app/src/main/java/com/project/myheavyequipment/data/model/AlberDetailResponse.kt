package com.project.myheavyequipment.data.model

import com.google.gson.annotations.SerializedName

data class AlberDetailResponse(

    @field:SerializedName("payload")
    val payload: PayloadItem? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)