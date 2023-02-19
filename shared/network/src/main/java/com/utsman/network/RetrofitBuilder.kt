package com.utsman.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder(private val tokenizer: Tokenizer) {
    companion object {
        private const val BASE_URL = "https://nasty-turkey-78.telebit.io"
    }

    private fun okHttp(isRequiredToken: Boolean): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(logInterceptor)
            .callTimeout(2 * 60, TimeUnit.SECONDS)
            .connectTimeout(2 * 60, TimeUnit.SECONDS)
            .readTimeout(2 * 60, TimeUnit.SECONDS)
            .writeTimeout(2 * 60, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
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