package com.utsman.ojeku.booking

import com.google.gson.annotations.SerializedName

data class RejectBookingResponse(
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var `data`: Boolean? = null
)