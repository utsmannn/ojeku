package com.utsman.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    companion object {
        private const val BASE_URL = "https://3688-2001-448a-2020-19df-3088-2025-6172-2106.ap.ngrok.io"
    }

    private fun okHttp(): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(logInterceptor)
            .build()
    }

    fun build(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}