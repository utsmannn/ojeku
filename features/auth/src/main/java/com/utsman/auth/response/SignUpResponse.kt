package com.utsman.auth.response


import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var `data`: Boolean? = null
)