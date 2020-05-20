package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class SendChatActionRequest(
        @Json(name = "action")
        val action: String, // String
        @Json(name = "chat_id")
        val chatId: Long // String
)