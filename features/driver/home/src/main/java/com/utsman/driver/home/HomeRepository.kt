package com.utsman.driver.home

import android.location.Location
import com.utsman.core.LocationManager
import com.utsman.core.extensions.mapStateEvent
import com.utsman.core.state.StateEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface HomeRepository {
    val locationResult: StateFlow<StateEvent<Location>>

    suspend fun getLocation()

    private class Impl(
        private val locationManager: LocationManager
    ) : HomeRepository {

        private val _locationResult: MutableStateFlow<StateEvent<Location>> =
            MutableStateFlow(StateEvent.Idle())
        override val locationResult: StateFlow<StateEvent<Location>>
            get() = _locationResult

        override suspend fun getLocation() {
            locationManager.getLocationFlow()
                .mapStateEvent()
                .collect(_locationResult)
        }
    }

    companion object {
        fun build(locationManager: LocationManager): HomeRepository {
            return Impl(locationManager)
        }
    }
}