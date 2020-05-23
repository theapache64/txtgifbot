package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json

data class Update(
        @Json(name = "message")
        val message: Message,
        @Json(name = "update_id")
        val updateId: Int // 511906534
) {
    data class Message(
            @Json(name = "animation")
            val animation: VideoOrAnimation?,
            @Json(name = "video")
            val video: VideoOrAnimation?,
            @Json(name = "text")
            val text: String?,
            @Json(name = "reply_to_message")
            val replyToMessage: ReplyToMessage?,
            @Json(name = "chat")
            val chat: Chat,
            @Json(name = "date")
            val date: Int, // 1589815495
            @Json(name = "from")
            val from: From,
            @Json(name = "message_id")
            val messageId: Long // 6
    ) {
        data class ReplyToMessage(
                @Json(name = "animation")
                val animation: VideoOrAnimation?,
                @Json(name = "video")
                val video: VideoOrAnimation?,
                @Json(name = "chat")
                val chat: Chat,
                @Json(name = "date")
                val date: Int, // 1589817141
                @Json(name = "from")
                val from: From,
                @Json(name = "message_id")
                val messageId: Long // 15
        ) {


            data class Chat(
                    @Json(name = "first_name")
                    val firstName: String?, // theapache64
                    @Json(name = "id")
                    val id: Int, // 240810054
                    @Json(name = "type")
                    val type: String, // private
                    @Json(name = "username")
                    val username: String? // theapache64
            )

            data class From(
                    @Json(name = "first_name")
                    val firstName: String?, // theapache64
                    @Json(name = "id")
                    val id: Int, // 240810054
                    @Json(name = "is_bot")
                    val isBot: Boolean, // false
                    @Json(name = "language_code")
                    val languageCode: String?, // en
                    @Json(name = "username")
                    val username: String? // theapache64
            )
        }

        data class VideoOrAnimation(
                @Json(name = "duration")
                val duration: Int, // 2
                @Json(name = "file_id")
                val fileId: String, // CgACAgQAAxkBAAMGXsKox9If0bciBetW4DQ1vID1CD4AAicCAALC-QRS18h1tYcTY8cZBA
                @Json(name = "file_name")
                val fileName: String?, // giphy.mp4
                @Json(name = "file_size")
                val fileSize: Int, // 751820
                @Json(name = "file_unique_id")
                val fileUniqueId: String, // AgADJwIAAsL5BFI
                @Json(name = "height")
                val height: Int, // 376
                @Json(name = "mime_type")
                val mimeType: String, // video/mp4
                @Json(name = "thumb")
                val thumb: Thumb,
                @Json(name = "width")
                val width: Int // 480
        ) {
            data class Thumb(
                    @Json(name = "file_id")
                    val fileId: String, // AAMCBAADGQEAAwZewqjH0h_RtyIF61bgNDW8gPUIPgACJwIAAsL5BFLXyHW1hxNjx_A0PBsABAEAB20AA28bAAIZBA
                    @Json(name = "file_size")
                    val fileSize: Int, // 19796
                    @Json(name = "file_unique_id")
                    val fileUniqueId: String, // AQAD8DQ8GwAEbxsAAg
                    @Json(name = "height")
                    val height: Int, // 251
                    @Json(name = "width")
                    val width: Int // 320
            )
        }

        data class Chat(
                @Json(name = "first_name")
                val firstName: String?
                , // theapache64
                @Json(name = "id")
                val id: Long, // 240810054
                @Json(name = "type")
                val type: String, // private
                @Json(name = "username")
                val username: String? // theapache64
        )

        data class From(
                @Json(name = "first_name")
                val firstName: String?, // theapache64
                @Json(name = "id")
                val id: Long, // 240810054
                @Json(name = "is_bot")
                val isBot: Boolean, // false
                @Json(name = "language_code")
                val languageCode: String, // en
                @Json(name = "username")
                val username: String? // theapache64
        )
    }
}