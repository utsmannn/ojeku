package com.utsman.locationapi.ui

import android.location.Location
import androidx.core.view.isVisible
import com.google.android.gms.maps.model.LatLng
import com.utsman.core.extensions.toLocation
import com.utsman.locationapi.R
import com.utsman.locationapi.databinding.ItemSearchLocationBinding
import com.utsman.locationapi.entity.LocationData
import com.utsman.utils.orNol
import kotlin.math.roundToInt

fun ItemSearchLocationBinding.onBindAdapter(
    position: Int,
    item: LocationData,
    currentLocation: Location,
    itemCount: Int,
    onClick: (Int, LocationData) -> Unit,
    onToggleClick: () -> Unit,
    onBind: (ItemSearchLocationBinding) -> Unit = {}
) {
    itemTvName.text = item.name
    itemTvAddress.text = item.address

    val distance = item.latLng.toLocation().distanceTo(currentLocation)
    val distanceIsMeters = when {
        distance < 1000.0 -> "~ ${distance.roundToInt()} M"
        else -> String.format("~ %.2f KM", distance / 1000.0)
    }

    itemTvDistance.text = distanceIsMeters

    val isLastPosition = position == itemCount - 1
    itemDivider.isVisible = !isLastPosition

    val bookmarkDrawable = if (item.isSaved) {
        R.drawable.ic_bookmark_filled
    } else {
        R.drawable.ic_bookmark
    }

    imgBookmark.setImageResource(bookmarkDrawable)

    root.setOnClickListener {
        onClick.invoke(position, item)
    }

    imgBookmark.setOnClickListener {
        onToggleClick.invoke()
    }
}

// LatLng
fun String.toLatLng(): LatLng {
   // [lat/lng: (-6.17357,
    // 106.84321)

    val listCoordinateRaw = split(",")
    val latRaw = listCoordinateRaw[0]
    val lonRaw = listCoordinateRaw[1]

    val latString = latRaw.split("(")[1]
    val lonString = lonRaw.split(")")[0]

    return LatLng(latString.toDoubleOrNull().orNol(), lonString.toDoubleOrNull().orNol())
}