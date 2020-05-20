package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class SendChatActionResponse(
        @Json(name = "ok")
        val ok: Boolean, // true
        @Json(name = "result")
        val result: Boolean // true
)