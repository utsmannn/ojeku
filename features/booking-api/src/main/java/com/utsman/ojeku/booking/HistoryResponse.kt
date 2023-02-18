package com.utsman.ojeku.booking


import com.google.gson.annotations.SerializedName

data class HistoryResponse(
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
        @SerializedName("price")
        var price: Double? = null,
        @SerializedName("trans_type")
        var transType: String? = null,
        @SerializedName("time")
        var time: TimeResponse? = null
    ) {
        data class TimeResponse(
            @SerializedName("local_date")
            var localDate: String? = null,
            @SerializedName("millis")
            var millis: Long? = null,
            @SerializedName("time_string")
            var timeString: String? = null
        )
    }
}