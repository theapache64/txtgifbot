package com.theapache64.tgb.servlets

import com.theapache64.telegrambot.api.di.modules.MoshiModule
import com.theapache64.telegrambot.api.di.modules.NetworkModule
import com.theapache64.telegrambot.api.di.modules.TelegramModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, TelegramModule::class])
interface TgbComponent {
    fun inject(tgbServlet: TgbServlet)
}