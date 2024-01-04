package com.project.myheavyequipment.data.remote

import com.project.myheavyequipment.data.model.AlberHistoryResponse
import com.project.myheavyequipment.data.model.AlberListResponse
import com.project.myheavyequipment.data.model.DeleteResponse
import com.project.myheavyequipment.data.model.LoginResponse
import com.project.myheavyequipment.data.model.LogoutResponse
import com.project.myheavyequipment.data.model.RegisterResponse
import com.project.myheavyequipment.data.model.ScanResponse
import com.project.myheavyequipment.data.model.UpdateAlberReparationResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {
    @POST("api/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("api/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("api/items")
    suspend fun getAlberList(
        @Header("Authorization") token: String,
    ): AlberListResponse

    @GET("api/items/{index}")
    suspend fun getAlberDetail(
        @Header("Authorization") token: String,
        @Path("index") index: Int
    ): AlberListResponse

    @POST("api/items/scan")
    @FormUrlEncoded
    suspend fun scanQRCode(
        @Header("Authorization") token: String,
        @Field("unique_code") unique_code: String
    ): ScanResponse

    @GET("api/items/{index}/reparations")
    suspend fun getAlberReparationHistory(
        @Header("Authorization") token: String,
        @Path("index") index: Int
    ): AlberHistoryResponse

    @POST("api/items/{index}/reparations/{reparationIndex}/update")
    suspend fun updateAlberReparationToWork(
        @Header("Authorization") token: String,
        @Path("index") index: Int,
        @Path("reparationIndex") reparationIndex: Int,
    ): UpdateAlberReparationResponse

    @POST("api/items/{index}/reparations/store")
    @FormUrlEncoded
    suspend fun storeAlberReparation(
        @Header("Authorization") token: String,
        @Path("index") index: Int,
        @Field("hours_meter") hours_meter: Int,
        @Field("note") note: String?
    ): UpdateAlberReparationResponse

    @DELETE("api/items/{index}/delete")
    suspend fun deleteAlber(
        @Header("Authorization") token: String,
        @Path("index") index: Int,
    ): DeleteResponse

    @Streaming
    @POST("api/items/store")
    @FormUrlEncoded
    suspend fun postAlberToQRCode(
        @Header("Authorization") token: String,
        @Field("jenis") jenis: String,
        @Field("type") type: String,
        @Field("hours_meter") hours_meter: Int,
        @Field("capacity") capacity: Int,
        @Field("engine") engine: String,
        @Field("lifting_height") lifting_height: Int?,
        @Field("stage") stage: Int?,
        @Field("load_center") load_center: Int?,
    ): Response<ResponseBody>

    @POST("api/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): LogoutResponse
}