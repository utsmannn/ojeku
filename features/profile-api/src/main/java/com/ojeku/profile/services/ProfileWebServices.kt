package com.ojeku.profile.services

import com.ojeku.profile.entity.UpdateFcmRequest
import com.ojeku.profile.entity.UserLocationResponse
import com.ojeku.profile.entity.UserResponse
import com.utsman.network.RetrofitBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ProfileWebServices {

    @GET("/api/user")
    suspend fun getUser(): Response<UserResponse>

    @PUT("/api/user/fcm")
    suspend fun updateFcmToken(
        @Body fcmRequest: UpdateFcmRequest
    ): Response<UserResponse>

    @POST("/api/user/driver/active")
    suspend fun updateDriverActive(
        @Query("is_active") isActive: Boolean
    ): Response<UserResponse>

    @PUT("/api/user/location")
    suspend fun updateUserLocation(
        @Query("coordinate") coordinateString: String
    ): Response<UserLocationResponse>

    companion object : KoinComponent {
        private val retrofitBuilder: RetrofitBuilder by inject()
        fun build(): ProfileWebServices {
            return retrofitBuilder.build(true).create()
        }
    }
}