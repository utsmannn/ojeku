package com.utsman.ojeku.home

import android.location.Location
import com.utsman.core.state.FlowState
import com.utsman.core.state.StateEvent
import com.utsman.core.state.StateEventManager
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    val locationResult: StateEventManager<Location>

    val initialLocation: FlowState<LocationData>
    val locationFrom: FlowState<LocationData>
    val locationDestination: FlowState<LocationData>

    suspend fun reverseCurrentLocation(location: Location)

    fun setFromLocation(locationData: LocationData)
    fun setDestinationLocation(locationData: LocationData)

    suspend fun getLocation()
}