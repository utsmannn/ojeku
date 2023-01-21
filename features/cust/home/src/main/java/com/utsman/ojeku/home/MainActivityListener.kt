package com.utsman.ojeku.home

import android.location.Location
import com.utsman.locationapi.entity.LocationData
import com.utsman.utils.listener.ActivityListener

interface MainActivityListener : ActivityListener {
    fun onLocationResult(data: Location)

    fun sendFromLocation(from: LocationData)
    fun sendDestinationLocation(destination: LocationData)

    fun navigateToMain()
    fun navigateToSearchLocation(formType: Int)

    object FormType {
        const val FROM = 1
        const val DEST = 2
    }
}