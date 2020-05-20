package com.theapache64.telegrambot.api.data.remote.repos

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.theapache64.telegrambot.api.TelegramApi
import com.theapache64.telegrambot.api.di.TelegramBotToken
import com.theapache64.telegrambot.api.di.modules.NetworkModule
import com.theapache64.telegrambot.api.models.*
import com.theapache64.telegrambot.api.utils.StringUtils
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
        const val CHAT_ACTION_SENDING_DOCUMENT = "upload_document"
    }

    private val updateAdapter: JsonAdapter<Update> by lazy {
        moshi.adapter(Update::class.java)
    }

    fun parseUpdate(jsonString: String): Update? {
        return updateAdapter.fromJson(jsonString)
    }


    suspend fun sendMessage(sendMessageRequest: SendMessageRequest): SendMessageResponse {
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


    suspend fun sendAnimation(chatId: Long, file: File): SendAnimationResponse {
        val mediaType = MediaType.parse("multipart/form-data")
        val requestFile = RequestBody.create(mediaType, file)
        val photoPart = MultipartBody.Part.createFormData("animation", file.name, requestFile)
        val chatIdPart = RequestBody.create(mediaType, chatId.toString())
        return telegramApi.sendAnimationFile(
                accessToken,
                chatIdPart,
                photoPart
        )
    }

    suspend fun sendAnimation(request: SendAnimationRequest): Any {
        return telegramApi.sendAnimationFile(
                accessToken,
                request
        )
    }


    fun downloadGif(fileId: String): File? {

        val fileInfo = telegramApi.getFileInfo(accessToken, fileId).execute().body()!!

        if (fileInfo.ok) {
            val fileLink = "${NetworkModule.BASE_URL}file/bot$accessToken/${fileInfo.result.filePath}"
            println("File link is $fileLink")
            val tempGif = File("/tmp/txtgifbot/${StringUtils.toFileName("${fileId}_${fileInfo.result.filePath}")}")
            if (!tempGif.parentFile.exists()) {
                tempGif.parentFile.mkdirs()
            }
            URL(fileLink).openStream().copyTo(FileOutputStream(tempGif))
            println("File downloaded: ${tempGif.absolutePath} : ${tempGif.exists()}")
            return tempGif
        }
        return null
    }
}