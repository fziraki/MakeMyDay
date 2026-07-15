package com.github.fziraki.daykit.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.OkHttp

fun getEngine(): HttpClientEngine = OkHttp.create { }
