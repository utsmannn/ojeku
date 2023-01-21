package com.utsman.ojeku.booking


import com.google.gson.annotations.SerializedName

data class BookingResponse(
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
        @SerializedName("customer_id")
        var customerId: String? = null,
        @SerializedName("driver_id")
        var driverId: String? = null,
        @SerializedName("route_location")
        var routeLocation: RouteLocationResponse? = null,
        @SerializedName("price")
        var price: Double? = null,
        @SerializedName("status")
        var status: String? = null
    ) {
        data class RouteLocationResponse(
            @SerializedName("from")
            var from: LocationAddressResponse? = null,
            @SerializedName("destination")
            var destination: LocationAddressResponse? = null,
            @SerializedName("routes")
            var routes: RoutesResponse? = null
        ) {
            data class LocationAddressResponse(
                @SerializedName("name")
                var name: String? = null,
                @SerializedName("address")
                var address: AddressResponse? = null,
                @SerializedName("coordinate")
                var coordinate: CoordinateResponse? = null
            ) {
                data class AddressResponse(
                    @SerializedName("city")
                    var city: String? = null,
                    @SerializedName("country")
                    var country: String? = null,
                    @SerializedName("district")
                    var district: String? = null
                )

                data class CoordinateResponse(
                    @SerializedName("latitude")
                    var latitude: Double? = null,
                    @SerializedName("longitude")
                    var longitude: Double? = null
                )
            }

            data class RoutesResponse(
                @SerializedName("route")
                var route: List<RouteResponse?>? = null,
                @SerializedName("distance")
                var distance: Long? = null
            ) {
                data class RouteResponse(
                    @SerializedName("latitude")
                    var latitude: Double? = null,
                    @SerializedName("longitude")
                    var longitude: Double? = null
                )
            }
        }
    }
}