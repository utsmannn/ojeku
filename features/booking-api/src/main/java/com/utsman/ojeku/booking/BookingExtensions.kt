package com.utsman.ojeku.booking

import com.google.android.gms.maps.model.LatLng
import com.utsman.locationapi.entity.LocationData
import com.utsman.utils.orNol
import java.text.NumberFormat
import java.util.Locale

fun Booking.LocationAddress.toLocationData(): LocationData {
    return LocationData(
        name = this.name,
        address = "${this.address.district}, ${this.address.country}, ${this.address.city}",
        latLng = LatLng(this.coordinate.latitude.orNol(), this.coordinate.longitude.orNol())
    )
}

fun Double.rp(): String {
    val locale = Locale("in", "ID")
    return NumberFormat.getCurrencyInstance(locale).format(this).toString()
}