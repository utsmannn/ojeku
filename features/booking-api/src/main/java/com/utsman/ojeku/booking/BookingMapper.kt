package com.utsman.ojeku.booking

import com.utsman.utils.orNol

object BookingMapper {

    fun mapResponseToBooking(response: BookingResponse?): Booking {
        val data = response?.data
        val routeLocationResponse = data?.routeLocation
        val routeLocationAddressFromResponse = routeLocationResponse?.from
        val routeLocationAddressDestResponse = routeLocationResponse?.destination

        val locationAddressFromData = Booking.LocationAddress(
            name = routeLocationAddressFromResponse?.name.orEmpty(),
            coordinate = Booking.Coordinate(
                latitude = routeLocationAddressFromResponse?.coordinate?.latitude ?: 0.0,
                longitude = routeLocationAddressFromResponse?.coordinate?.longitude ?: 0.0
            )
        )

        val locationAddressDestData = Booking.LocationAddress(
            name = routeLocationAddressDestResponse?.name.orEmpty(),
            coordinate = Booking.Coordinate(
                latitude = routeLocationAddressDestResponse?.coordinate?.latitude ?: 0.0,
                longitude = routeLocationAddressDestResponse?.coordinate?.longitude ?: 0.0
            )
        )

        val coordinate = routeLocationResponse?.routes?.route.orEmpty().map {
            Booking.Coordinate(it?.latitude.orNol(), it?.longitude.orNol())
        }

        val distance = routeLocationResponse?.routes?.distance.orNol()

        val routeData = Booking.RouteLocation(
            from = locationAddressFromData,
            destination = locationAddressDestData,
            routes = Booking.Routes(coordinate, distance)
        )

        return Booking(
            id = data?.id.orEmpty(),
            customerId = data?.customerId.orEmpty(),
            driverId = data?.driverId.orEmpty(),
            routeLocation = routeData,
            price = data?.price ?: 0.0,
            status = Booking.BookingStatus.valueOf(
                data?.status ?: Booking.BookingStatus.UNDEFINE.name
            )
        )
    }
}