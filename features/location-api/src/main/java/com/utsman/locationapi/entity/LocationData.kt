package com.utsman.locationapi.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.utsman.core.data.EquatableProvider
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    var id: Int = 0,
    var name: String = "",
    var address: String = "",
    var latLng: LatLng = LatLng(0.0, 0.0),
    var isSaved: Boolean = false,
    var isRecent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable, EquatableProvider("$name-$address-$latLng")