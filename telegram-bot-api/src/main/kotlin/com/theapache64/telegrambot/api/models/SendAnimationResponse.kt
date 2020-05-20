package com.theapache64.telegrambot.api.models

import com.squareup.moshi.Json


data class SendAnimationResponse(
        @Json(name = "ok")
        val ok: Boolean, // true
        @Json(name = "result")
        val result: Result
) {
    data class Result(
            @Json(name = "document")
            val document: Document
    ) {
        data class Document(
                @Json(name = "file_id")
                val fileId: String // CgACAgUAAxkDAAMzXsV9gKdEj7UBobhE6udfdn9QdTcAAjkBAAK1NChWiW47eJPHHjQZBA
        )
    }
}