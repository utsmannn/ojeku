package com.utsman.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {
    companion object {
        private const val BASE_URL = "https://e9d4-2001-448a-2022-1f5c-380d-cce9-1b4-9be2.ap.ngrok.io"
    }

    private fun okHttp(): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(logInterceptor)
            .build()
    }

    private fun gson() = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()

    fun build(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp())
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()
    }
}