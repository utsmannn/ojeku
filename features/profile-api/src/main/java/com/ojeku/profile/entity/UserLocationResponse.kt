package com.ojeku.profile.entity


import com.google.gson.annotations.SerializedName

data class UserLocationResponse(
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var `data`: DataResponse? = null
) {
    data class DataResponse(
        @SerializedName("id")
        var id: String? = null,
        @SerializedName("coordinate")
        var coordinate: CoordinateResponse? = null
    ) {
        data class CoordinateResponse(
            @SerializedName("lat")
            var latitude: Double? = null,
            @SerializedName("lng")
            var longitude: Double? = null
        )
    }
}