package com.utsman.locationapi.ui

import android.location.Location
import androidx.core.view.isVisible
import com.utsman.core.extensions.toLocation
import com.utsman.locationapi.databinding.ItemSearchLocationBinding
import com.utsman.locationapi.entity.LocationData
import kotlin.math.roundToInt

fun ItemSearchLocationBinding.onBindAdapter(
    position: Int,
    item: LocationData,
    currentLocation: Location,
    itemCount: Int,
    onClick: (Int, LocationData) -> Unit
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

    root.setOnClickListener {
        onClick.invoke(position, item)
    }
}