package com.theapache64.telegrambot.api.di.modules

import com.squareup.moshi.Moshi
import com.theapache64.telegrambot.api.TelegramApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [MoshiModule::class])
class NetworkModule {

    companion object {
        const val BASE_URL = "https://api.telegram.org/"
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
    }

    @Singleton
    @Provides
    fun provideTelegramApi(retrofit: Retrofit): TelegramApi {
        return retrofit.create(TelegramApi::class.java)
    }
}