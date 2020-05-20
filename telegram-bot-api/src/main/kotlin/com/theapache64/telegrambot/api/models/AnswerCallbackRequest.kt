package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json

data class AnswerCallbackRequest(
        @Json(name = "callback_query_id")
        val callbackQueryId: String // 123
)