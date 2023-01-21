package com.utsman.ojeku.booking

import com.utsman.network.RetrofitBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BookingWebServices {

    @GET("/api/booking")
    suspend fun getCurrentBookingCustomer(
        @Query("status") status: Booking.BookingStatus
    ): Response<BookingResponse>

    @POST("/api/booking")
    suspend fun postBookingCustomer(
        @Query("from") from: String,
        @Query("destination") destination: String
    ): Response<BookingResponse>

    @POST("/api/booking/request")
    suspend fun requestBookingCustomer(
        @Query("booking_id") bookingId: String
    ): Response<BookingResponse>

    @POST("/api/booking/cancel")
    suspend fun cancelBookingCustomer(
        @Query("booking_id") bookingId: String
    ): Response<BookingResponse>

    companion object : KoinComponent {
        private val retrofitBuilder: RetrofitBuilder by inject()
        fun build(): BookingWebServices {
            return retrofitBuilder.build(true).create()
        }
    }
}