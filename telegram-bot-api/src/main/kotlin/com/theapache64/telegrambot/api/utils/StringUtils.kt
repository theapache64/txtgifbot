package com.theapache64.telegrambot.api.utils

object StringUtils {

    private val anyNonWordChar by lazy { "[^a-zA-Z0-9\\.]".toRegex() }
    private val twoOrMoreUnderscore by lazy { "_{2,}".toRegex() }

    fun toFileName(text: String): String {
        return text.replace(anyNonWordChar, "_").replace(twoOrMoreUnderscore, "_")
    }
}