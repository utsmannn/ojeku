package com.utsman.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder(private val tokenizer: Tokenizer) {
    companion object {
        private const val BASE_URL = "https://ed93-2001-448a-2020-7b42-9d98-1d93-9831-a4d3.ap.ngrok.io"
    }

    private fun okHttp(isRequiredToken: Boolean): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(logInterceptor)
            .run {
                if (isRequiredToken) {
                    addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                            .header("Authorization", tokenizer.token)
                            .build()

                        chain.proceed(newRequest)
                    }
                } else {
                    this
                }
            }
            .build()
    }

    private fun gson() = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()

    fun build(isRequiredToken: Boolean = false): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp(isRequiredToken))
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .build()
    }
}