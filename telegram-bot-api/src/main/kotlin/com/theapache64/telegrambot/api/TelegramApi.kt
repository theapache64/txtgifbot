package com.theapache64.telegrambot.api

import com.theapache64.telegrambot.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TelegramApi {

    @POST("/bot{from}/sendMessage")
    suspend fun sendMessage(@Path("from") from: String, @Body sendMessageRequest: SendMessageRequest): SendMessageResponse

    @POST("/bot{from}/sendChatAction")
    suspend fun sendChatAction(@Path("from") from: String, @Body sendMessageRequest: SendChatActionRequest): SendChatActionResponse

    @Multipart
    @POST("/bot{from}/sendAnimation")
    suspend fun sendAnimationFile(
            @Path("from") from: String,
            @Part("chat_id") chatId: RequestBody,
            @Part animation: MultipartBody.Part
    ): Any

    @GET("/bot{from}/getFile")
    fun getFileInfo(
            @Path("from") from: String,
            @Query("file_id") fileId: String
    ): Call<GetFileInfoResponse>
}