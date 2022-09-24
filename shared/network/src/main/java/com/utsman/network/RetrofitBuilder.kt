package com.utsman.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    companion object {
        private const val BASE_URL = "https://55c4-2001-448a-2020-403d-b5ed-29cd-e0ab-a223.ap.ngrok.io"
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