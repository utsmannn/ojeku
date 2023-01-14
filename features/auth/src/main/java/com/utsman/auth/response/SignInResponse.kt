package com.utsman.auth.response


import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var `data`: DataResponse? = null
) {
    data class DataResponse(
        @SerializedName("token")
        var token: String? = null
    )
}