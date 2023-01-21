package com.utsman.locationapi.response


import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Data(
        @SerializedName("address")
        val address: Address?,
        @SerializedName("coordinate")
        val coordinate: Coordinate?,
        @SerializedName("name")
        val name: String?
    ) {
        data class Address(
            @SerializedName("city")
            val city: String?,
            @SerializedName("country")
            val country: String?,
            @SerializedName("district")
            val distric: String?
        )

        data class Coordinate(
            @SerializedName("latitude")
            val latitude: Double?,
            @SerializedName("longitude")
            val longitude: Double?
        )
    }
}