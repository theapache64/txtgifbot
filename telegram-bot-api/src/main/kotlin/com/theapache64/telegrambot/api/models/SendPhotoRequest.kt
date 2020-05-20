package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class SendPhotoRequest(
        @Json(name = "chat_id")
        val chatId: Long, // 24234234234
        @Json(name = "photo")
        val photo: String, // 23423423423423
        @Json(name = "caption")
        val caption: String?
)