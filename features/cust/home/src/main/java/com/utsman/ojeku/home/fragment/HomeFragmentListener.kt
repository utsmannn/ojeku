package com.utsman.ojeku.home.fragment

import android.location.Location
import com.utsman.locationapi.entity.LocationData
import com.utsman.utils.listener.FragmentListener

interface HomeFragmentListener : FragmentListener {
    fun onMessageFromActivity(message: String)
    fun requestLocation()
    fun requestInitialData(location: Location)
    fun onDataLocation(from: LocationData, destination: LocationData)
    fun pushLoadingFormLocation()
}