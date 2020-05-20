package com.theapache64.telegrambot.api.data.remote.repos

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.theapache64.telegrambot.api.TelegramApi
import com.theapache64.telegrambot.api.di.TelegramBotToken
import com.theapache64.telegrambot.api.di.modules.NetworkModule
import com.theapache64.telegrambot.api.models.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

class TelegramRepo @Inject constructor(
        private val telegramApi: TelegramApi,
        private val moshi: Moshi,
        @TelegramBotToken
        private val accessToken: String
) {
    companion object {
        // Chat actions
        const val CHAT_ACTION_TYPING = "typing"
        const val CHAT_ACTION_SENDING_PHOTO = "upload_photo"
    }

    private val updateAdapter: JsonAdapter<Update> by lazy {
        moshi.adapter(Update::class.java)
    }

    fun parseUpdate(jsonString: String): Update? {
        return updateAdapter.fromJson(jsonString)
    }


    suspend fun sendMessage(sendMessageRequest: SendMessageRequest): SendMessageResponse {
        println("Sending message using Telegram API : $accessToken")
        return telegramApi.sendMessage(
                accessToken,
                sendMessageRequest
        )
    }

    suspend fun sendChatAction(request: SendChatActionRequest): SendChatActionResponse {
        return telegramApi.sendChatAction(
                accessToken,
                request
        )
    }

    suspend fun sendChatActionAsync(request: SendChatActionRequest): SendChatActionResponse {
        return telegramApi.sendChatAction(
                accessToken,
                request
        )
    }

    suspend fun answerCallbackQuery(request: AnswerCallbackRequest): Any {
        return telegramApi.answerCallbackQuery(
                accessToken,
                request
        )
    }

    suspend fun sendPhotoFile(chatId: Long, file: File, caption: String): SendPhotoResponse {
        val mediaType = MediaType.parse("multipart/form-data")
        val requestFile = RequestBody.create(mediaType, file)
        val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val chatIdPart = RequestBody.create(mediaType, chatId.toString())
        val captionPart = RequestBody.create(mediaType, caption)
        return telegramApi.sendPhotoFile(
                accessToken,
                chatIdPart,
                captionPart,
                photoPart
        )
    }

    suspend fun sendPhoto(request: SendPhotoRequest): SendPhotoResponse {
        return telegramApi.sendPhoto(
                accessToken,
                request
        )
    }

    fun downloadGif(fileId: String): File? {
        val fileInfo = telegramApi.getFileInfo(accessToken, fileId).execute().body()!!
        if (fileInfo.ok) {
            val fileLink = "${NetworkModule.BASE_URL}file/bot$accessToken/${fileInfo.result.filePath}"
            println("File link is $fileLink")
            val file = File("${fileId}_${fileInfo.result.filePath}")
            URL(fileLink).openStream().copyTo(FileOutputStream(file))
            println("File downloaded: ${file.absolutePath}")
            return file
        }
        return null
    }
}