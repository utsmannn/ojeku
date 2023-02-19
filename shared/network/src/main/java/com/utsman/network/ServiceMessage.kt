package com.utsman.network

import com.google.gson.annotations.SerializedName
import com.utsman.core.data.EquatableProvider

data class ServiceMessage(
    @SerializedName("type")
    val type: Type,
    @SerializedName("customer_id")
    val customerId: String = "",
    @SerializedName("driver_id")
    val driverId: String = "",
    @SerializedName("booking_id")
    val bookingId: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("json")
    val json: String = "{}"
) : EquatableProvider(bookingId) {

    enum class Type {
        GENERAL, BOOKING, BOOKING_REQUEST, BOOKING_UNAVAILABLE, BOOKING_LOCATION
    }

    companion object {
        fun parseFromData(data: Map<String, String>): ServiceMessage {
            val type = Type.valueOf(data["type"] ?: Type.GENERAL.name)
            val customerId = data["customer_id"].orEmpty()
            val driverId = data["driver_id"].orEmpty()
            val bookingId = data["booking_id"].orEmpty()
            val message = data["message"].orEmpty()
            val json = data["json"].orEmpty()

            return ServiceMessage(type, customerId, driverId, bookingId, message, json)
        }
    }
}