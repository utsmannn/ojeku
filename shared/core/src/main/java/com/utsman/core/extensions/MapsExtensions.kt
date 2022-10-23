package com.utsman.core.extensions

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun LatLng.toLocation(): Location {
    val location = Location(LocationManager.NETWORK_PROVIDER)
    location.latitude = latitude
    location.longitude = longitude
    return location
}