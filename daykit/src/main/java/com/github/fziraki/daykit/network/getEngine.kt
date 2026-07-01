package com.github.fziraki.daykit.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.logging.HttpLoggingInterceptor

fun getEngine(): HttpClientEngine = OkHttp.create {
    addInterceptor(
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    )
}
