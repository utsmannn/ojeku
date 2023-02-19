package com.utsman.network

import com.utsman.core.CoroutineBus
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            CoroutineBus.getInstance().post("app_unauthorized", 1)
        }
        return response
    }
}