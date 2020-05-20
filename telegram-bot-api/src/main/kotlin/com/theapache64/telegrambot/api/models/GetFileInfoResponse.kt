package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class GetFileInfoResponse(
        @Json(name = "ok")
        val ok: Boolean, // true
        @Json(name = "result")
        val result: Result
) {
    data class Result(
            @Json(name = "file_id")
            val fileId: String, // CgACAgQAAxkBAAOIXsP3YBNB-4Aw6WJfTUHma8ntzUEAAicCAALC-QRS18h1tYcTY8cZBA
            @Json(name = "file_path")
            val filePath: String, // animations/file_0.mp4
            @Json(name = "file_size")
            val fileSize: Int, // 751820
            @Json(name = "file_unique_id")
            val fileUniqueId: String // AgADJwIAAsL5BFI
    )
}