package com.utsman.locationapi.entity

import com.google.android.gms.maps.model.LatLng

data class LocationData(
    var name: String = "",
    var address: String = "",
    var latLng: LatLng = LatLng(0.0, 0.0)
)