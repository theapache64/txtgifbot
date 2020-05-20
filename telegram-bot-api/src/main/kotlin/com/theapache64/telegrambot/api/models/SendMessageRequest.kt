package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class SendMessageRequest(
    @Json(name = "chat_id")
    val chatId: Long, // to
    @Json(name = "text")
    val text: String, // This is some message
    @Json(name = "disable_web_page_preview")
    val isDisableWebPagePreview: Boolean? = true,
    @Json(name = "parse_mode")
    val parseMode: String? = "HTML",
    @Json(name = "reply_to_message_id")
    val replyMsgId: Long? = null,
    @Json(name = "reply_markup")
    val replyMarkup: ReplyMarkup? = null
) {
    data class ReplyMarkup(
        @Json(name = "inline_keyboard")
        val inlineKeyboard: List<List<InlineButton>>? = null,
        @Json(name = "force_reply")
        val isForceReply: Boolean? = null,
        @Json(name = "keyboard")
        val keyboard: List<List<KeyboardButton>>? = null,
        @Json(name = "one_time_keyboard")
        val isOneTime : Boolean? = null
    )

    data class KeyboardButton(
        @Json(name = "text")
        val text: String
    )

    data class InlineButton(
        @Json(name = "text")
        val text: String, // ✅ Relevant
        @Json(name = "callback_data")
        val callbackData: String // r123
    ) {
        class ByteOverflowException(message: String?) : Throwable(message)

        init {
            val byteSize = callbackData.toByteArray().size
            if (byteSize > 64) {
                throw ByteOverflowException(
                    "Callback data exceeded `$callbackData`"
                )
            }
        }
    }
}