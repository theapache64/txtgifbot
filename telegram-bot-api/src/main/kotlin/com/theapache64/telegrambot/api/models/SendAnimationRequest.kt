package com.theapache64.telegrambot.api.models
import com.squareup.moshi.Json


data class SendAnimationRequest(
    @Json(name = "animation")
    val animation: String,
    @Json(name = "caption")
    val caption: String,
    @Json(name = "chat_id")
    val chatId: String
)