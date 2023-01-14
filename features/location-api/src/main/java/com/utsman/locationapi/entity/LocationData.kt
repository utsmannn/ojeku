package com.utsman.locationapi.entity

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    var name: String = "",
    var address: String = "",
    var latLng: LatLng = LatLng(0.0, 0.0)
): Parcelable