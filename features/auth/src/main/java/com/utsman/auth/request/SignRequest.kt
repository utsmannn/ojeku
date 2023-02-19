package com.utsman.auth.request


import com.google.gson.annotations.SerializedName

data class SignRequest(
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("vehicles_number")
    var vehiclesNumber: String? = null
)