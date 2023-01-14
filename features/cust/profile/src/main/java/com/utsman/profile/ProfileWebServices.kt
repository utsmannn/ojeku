package com.utsman.profile

import com.utsman.network.RetrofitBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import retrofit2.create
import retrofit2.http.GET

interface ProfileWebServices {

    @GET("/api/user")
    suspend fun getUser(): Response<UserResponse>

    companion object : KoinComponent {
        private val retrofitBuilder: RetrofitBuilder by inject()
        fun build(): ProfileWebServices {
            return retrofitBuilder.build(true).create()
        }
    }
}