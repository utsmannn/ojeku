package com.utsman.ojeku.booking


import com.google.gson.annotations.SerializedName

data class BookingCancelReasonResponse(
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var `data`: List<DataResponse?>? = null
) {
    data class DataResponse(
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("name")
        var name: String? = null
    )
}