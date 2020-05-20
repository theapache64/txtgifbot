package com.theapache64.telegrambot.api

import com.theapache64.telegrambot.api.models.*
import com.theapache64.telegrambot.api.models.AnswerCallbackRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TelegramApi {

    @POST("/bot{from}/sendMessage")
    suspend fun sendMessage(@Path("from") from: String, @Body sendMessageRequest: SendMessageRequest): SendMessageResponse

    @POST("/bot{from}/sendChatAction")
    suspend fun sendChatAction(@Path("from") from: String, @Body sendMessageRequest: SendChatActionRequest): SendChatActionResponse

    @POST("/bot{from}/answerCallbackQuery")
    suspend fun answerCallbackQuery(@Path("from") from: String, @Body answerCallbackRequest: AnswerCallbackRequest): Any

    @Multipart
    @POST("/bot{from}/sendPhoto")
    suspend fun sendPhotoFile(
            @Path("from") from: String,
            @Part("chat_id") chatId: RequestBody,
            @Part("caption") caption: RequestBody,
            @Part photo: MultipartBody.Part
    ): SendPhotoResponse

    @POST("/bot{from}/sendPhoto")
    suspend fun sendPhoto(
            @Path("from") from: String,
            @Body sendPhotoRequest: SendPhotoRequest
    ): SendPhotoResponse

    @GET("/bot{from}/getFile")
    fun getFileInfo(
            @Path("from") from: String,
            @Query("file_id") fileId: String
    ): Call<GetFileInfoResponse>
}