package com.utsman.core.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toIcon
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun LatLng.toLocation(): Location {
    val location = Location(LocationManager.NETWORK_PROVIDER)
    location.latitude = latitude
    location.longitude = longitude
    return location
}

fun Int.toBitmap(context: Context): Bitmap? {

    val drawable = ContextCompat.getDrawable(context, this) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth*2, drawable.intrinsicHeight*2)
    val bm = Bitmap.createBitmap(drawable.intrinsicWidth*2, drawable.intrinsicHeight*2, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bm)
    drawable.draw(canvas)
    return bm
}

fun MarkerOptions.iconResVector(iconRes: Int, context: Context): MarkerOptions {
    val bitmap = iconRes.toBitmap(context)
    return if (bitmap != null) {
        icon(BitmapDescriptorFactory.fromBitmap(bitmap))
    } else {
        this
    }
}