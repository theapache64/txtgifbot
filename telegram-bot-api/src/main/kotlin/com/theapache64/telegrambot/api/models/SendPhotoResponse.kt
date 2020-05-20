package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class SendPhotoResponse(
        @Json(name = "ok")
        val ok: Boolean, // true
        @Json(name = "result")
        val result: Result
) {
    data class Result(
            @Json(name = "chat")
            val chat: Chat,
            @Json(name = "date")
            val date: Int, // 1585527405
            @Json(name = "from")
            val from: From,
            @Json(name = "message_id")
            val messageId: Int, // 681
            @Json(name = "photo")
            val photo: List<Photo>
    ) {
        data class Chat(
                @Json(name = "first_name")
                val firstName: String?, // theapache64
                @Json(name = "id")
                val id: Int, // 240810054
                @Json(name = "type")
                val type: String, // private
                @Json(name = "username")
                val username: String?// theapache64
        )

        data class From(
                @Json(name = "first_name")
                val firstName: String?, // CoDoc19Dev
                @Json(name = "id")
                val id: Int, // 1028883028
                @Json(name = "is_bot")
                val isBot: Boolean, // true
                @Json(name = "username")
                val username: String?// CoDoc19DevBot
        )

        data class Photo(
                @Json(name = "file_id")
                val fileId: String, // AgACAgUAAxkDAAICqV6BOm2t1ohdMZdDkdng5z-OpOfEAAJVqTEbBQ8IVAMwpsGYHKPveK0lMwAEAQADAgADbQADlzUFAAEYBA
                @Json(name = "file_size")
                val fileSize: Int, // 5448
                @Json(name = "file_unique_id")
                val fileUniqueId: String, // AQADeK0lMwAElzUFAAE
                @Json(name = "height")
                val height: Int, // 225
                @Json(name = "width")
                val width: Int // 320
        )
    }
}