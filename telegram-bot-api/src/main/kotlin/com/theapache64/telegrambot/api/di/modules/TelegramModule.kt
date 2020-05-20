package com.theapache64.telegrambot.api.di.modules

import com.theapache64.telegrambot.api.di.TelegramBotToken
import dagger.Module
import dagger.Provides

@Module
class TelegramModule(private val telegramBotToken: String) {

    @Provides
    @TelegramBotToken
    fun provideBotToken(): String {
        return telegramBotToken
    }
}