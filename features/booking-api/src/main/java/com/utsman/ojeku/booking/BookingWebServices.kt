package com.utsman.ojeku.booking

import com.utsman.network.RetrofitBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingWebServices {

    @GET("/api/booking")
    suspend fun getCurrentBookingCustomer(
        @Query("status") status: Booking.BookingStatus
    ): Response<BookingResponse>

    @POST("/api/booking/customer/create")
    suspend fun postBookingCustomer(
        @Query("from") from: String,
        @Query("destination") destination: String
    ): Response<BookingResponse>

    @POST("/api/booking/customer/request")
    suspend fun requestBookingCustomer(
        @Query("booking_id") bookingId: String,
        @Query("trans_type") transType: Booking.TransType
    ): Response<BookingResponse>

    @POST("/api/booking/customer/cancel")
    suspend fun cancelBookingCustomer(
        @Query("booking_id") bookingId: String
    ): Response<BookingResponse>

    @GET("/api/booking/{booking_id}")
    suspend fun getBookingById(
        @Path("booking_id") bookingId: String
    ): Response<BookingResponse>

    @POST("/api/booking/driver/reject")
    suspend fun rejectBookingDriver(
        @Query("booking_id") bookingId: String
    ): Response<RejectBookingResponse>

    @POST("/api/booking/driver/accept")
    suspend fun acceptBookingDriver(
        @Query("booking_id") bookingId: String
    ): Response<BookingResponse>

    companion object : KoinComponent {
        private val retrofitBuilder: RetrofitBuilder by inject()
        fun build(): BookingWebServices {
            return retrofitBuilder.build(true).create()
        }
    }
}