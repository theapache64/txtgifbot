package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class CallbackQueryResponse(
        @Json(name = "callback_query")
        val callbackQuery: CallbackQuery,
        @Json(name = "update_id")
        val updateId: Int // 996097080
) {
    data class CallbackQuery(
            @Json(name = "chat_instance")
            val chatInstance: String, // -4027463488092007398
            @Json(name = "data")
            val `data`: String, // r123
            @Json(name = "from")
            val from: From,
            @Json(name = "id")
            val id: String, // 1034271309301426903
            @Json(name = "message")
            val message: Message
    ) {
        data class From(
                @Json(name = "first_name")
                val firstName: String?, // theapache64
                @Json(name = "id")
                val id: Int, // 240810054
                @Json(name = "is_bot")
                val isBot: Boolean, // false
                @Json(name = "language_code")
                val languageCode: String, // en
                @Json(name = "username")
                val username: String?// theapache64
        )

        data class Message(
                @Json(name = "chat")
                val chat: Chat,
                @Json(name = "date")
                val date: Int, // 1584998447
                @Json(name = "from")
                val from: From,
                @Json(name = "message_id")
                val messageId: Long, // 61
                @Json(name = "reply_markup")
                val replyMarkup: ReplyMarkup,
                @Json(name = "text")
                val text: String // Was it helpful? 😊
        ) {
            data class Chat(
                    @Json(name = "first_name")
                    val firstName: String?, // theapache64
                    @Json(name = "id")
                    val id: Long, // 240810054
                    @Json(name = "type")
                    val type: String, // private
                    @Json(name = "username")
                    val username: String?// theapache64
            )

            data class From(
                    @Json(name = "first_name")
                    val firstName: String?, // Corona Scholar - Dev
                    @Json(name = "id")
                    val id: Int, // 1119620721
                    @Json(name = "is_bot")
                    val isBot: Boolean, // true
                    @Json(name = "username")
                    val username: String?// corona_scholar_dev_bot
            )

            data class ReplyMarkup(
                    @Json(name = "inline_keyboard")
                    val inlineKeyboard: List<Any>
            )
        }
    }
}