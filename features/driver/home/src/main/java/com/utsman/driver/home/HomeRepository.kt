package com.utsman.driver.home

import android.location.Location
import com.utsman.core.LocationManager
import com.utsman.core.RepositoryProvider
import com.utsman.core.extensions.mapStateEvent
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.LocationWebServices
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface HomeRepository {
    val locationResult: StateFlow<StateEvent<Location>>
    val currentLocation: StateFlow<StateEvent<LocationData>>

    suspend fun getLocation()
    suspend fun reverseCurrentLocation(location: Location)

    private class Impl(
        private val locationManager: LocationManager,
        private val locationServices: LocationWebServices
    ) : HomeRepository, RepositoryProvider() {

        private val _locationResult: MutableStateFlow<StateEvent<Location>> =
            MutableStateFlow(StateEvent.Idle())
        override val locationResult: StateFlow<StateEvent<Location>>
            get() = _locationResult

        private val _currentLocation: MutableStateFlow<StateEvent<LocationData>> =
            MutableStateFlow(StateEvent.Idle())

        override val currentLocation: StateFlow<StateEvent<LocationData>>
            get() = _currentLocation

        override suspend fun getLocation() {
            locationManager.getLocationFlow()
                .mapStateEvent()
                .collect(_locationResult)
        }

        override suspend fun reverseCurrentLocation(location: Location) {
            val coordinateString = "${location.latitude},${location.longitude}"
            bindToState(
                stateFlow = _currentLocation,
                onFetch = {
                    locationServices.reverseLocation(coordinateString)
                },
                mapper = {
                    Mapper.mapReverseLocationResponseToData(it)
                }
            )
        }
    }

    companion object {
        fun build(locationManager: LocationManager, locationServices: LocationWebServices): HomeRepository {
            return Impl(locationManager, locationServices)
        }
    }
}