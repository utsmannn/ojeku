package com.utsman.profile


import com.google.gson.annotations.SerializedName

data class UserResponse(
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
        @SerializedName("username")
        var username: String? = null,
        @SerializedName("role")
        var role: String? = null,
        @SerializedName("fcm_token")
        var fcmToken: String? = null,
        @SerializedName("extra")
        var extra: ExtraResponse? = null
    ) {
        class ExtraResponse
    }
}