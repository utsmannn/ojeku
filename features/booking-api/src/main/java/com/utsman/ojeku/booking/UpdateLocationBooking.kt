package com.utsman.ojeku.booking

data class UpdateLocationBooking(
    val driver: UpdateLocationCoordinate,
    val route: UpdateLocationRoutes? = null
) {

    data class UpdateLocationRoutes(
        val route: List<UpdateLocationCoordinate> = emptyList(),
        val distance: Long = 0L
    ) {
        fun toRoutes(): Booking.Routes {
            return Booking.Routes(
                distance = distance,
                route = route.map { it.toCoordinate() }
            )
        }
    }

    data class UpdateLocationCoordinate(
        val lat: Double,
        val lng: Double
    ) {
        fun toCoordinate(): Booking.Coordinate {
            return Booking.Coordinate(lat, lng)
        }
    }
}