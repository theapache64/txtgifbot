package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class SendMessageResponse(
        @Json(name = "ok")
        val ok: Boolean?, // true
        @Json(name = "result")
        val result: Result?
) {
    data class Result(
            @Json(name = "chat")
            val chat: Chat?,
            @Json(name = "date")
            val date: Int?, // 1585252533
            @Json(name = "from")
            val from: From?,
            @Json(name = "message_id")
            val messageId: Long, // 84
            @Json(name = "text")
            val text: String? // Hey
    ) {
        data class Chat(
                @Json(name = "first_name")
                val firstName: String?, // theapache64
                @Json(name = "id")
                val id: Int?, // 240810054
                @Json(name = "type")
                val type: String?, // private
                @Json(name = "username")
                val username: String? // theapache64
        )

        data class From(
                @Json(name = "first_name")
                val firstName: String?, // CoDoc19Dev
                @Json(name = "id")
                val id: Int?, // 1028883028
                @Json(name = "is_bot")
                val isBot: Boolean?, // true
                @Json(name = "username")
                val username: String? // CoDoc19DevBot
        )
    }
}