package com.utsman.ojeku

import android.location.Location
import com.utsman.core.state.MutableStateEventManager
import com.utsman.core.state.StateEventManager
import com.utsman.core.LocationManager
import com.utsman.core.extensions.mapStateEvent
import com.utsman.core.state.StateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(
    private val locationManager: LocationManager
) : HomeRepository {

    private val _locationResult = MutableStateEventManager<Location>()
    override val locationResult: StateEventManager<Location>
        get() = _locationResult

    override suspend fun getLocation() {
        locationManager.getLocationFlow()
            .mapStateEvent()
            .collect(_locationResult)
    }
}