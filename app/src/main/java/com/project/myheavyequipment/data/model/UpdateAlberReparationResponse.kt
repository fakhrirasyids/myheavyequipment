package com.project.myheavyequipment.data.model

import com.google.gson.annotations.SerializedName

data class UpdateAlberReparationResponse(

    @field:SerializedName("payload")
    val payload: Payload? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class UserReparation(

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("email_verified_at")
    val emailVerifiedAt: Any? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null
)

data class Payload(

    @field:SerializedName("note")
    val note: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("item_id")
    val itemId: String? = null,

    @field:SerializedName("reparation_date")
    val reparationDate: Any? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user")
    val user: UserReparation? = null,

    @field:SerializedName("hours_meter")
    val hoursMeter: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
