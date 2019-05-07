package com.test.teamvoy.sunrise.networking.api

import com.test.teamvoy.sunrise.networking.base.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient private constructor() {
    private val BASE_URL = "https://api.sunrise-sunset.org/"

    public var webService: WebService
    public var retrofit: Retrofit

    companion object {
        const val TIMEOUT = 20L

        fun getClient(): ApiClient {
            return ApiClient()
        }
    }

    init {
        val client = defaultClient()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
        webService = retrofit.create(WebService::class.java)
    }

    private fun defaultClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

}
