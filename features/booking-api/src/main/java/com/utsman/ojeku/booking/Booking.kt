package com.utsman.ojeku.booking

data class Booking(
    var id: String = "",
    var customerId: String = "",
    var driverId: String = "",
    var routeLocation: RouteLocation = RouteLocation(),
    var price: Double = 0.0,
    var status: BookingStatus = BookingStatus.READY,
    var transType: TransType = TransType.BIKE
) {

    data class RouteLocation(
        var from: LocationAddress = LocationAddress(),
        var destination: LocationAddress = LocationAddress(),
        var routes: Routes = Routes(emptyList(), 0L)
    )

    enum class BookingStatus {
        READY, REQUEST, ACCEPTED, CANCELED, ONGOING, DONE, UNDEFINE
    }

    enum class TransType {
        BIKE, CAR
    }

    data class LocationAddress(
        var name: String = "",
        var address: Address = Address(),
        var coordinate: Coordinate = Coordinate()
    ) {
        data class Address(
            var city: String = "",
            var country: String = "",
            var district: String = ""
        )
    }

    data class Coordinate(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    data class Routes(
        val route: List<Coordinate>,
        val distance: Long
    )
}