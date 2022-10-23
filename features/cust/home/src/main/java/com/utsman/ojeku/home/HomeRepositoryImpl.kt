package com.utsman.ojeku.home

import android.location.Location
import com.utsman.core.state.MutableStateEventManager
import com.utsman.core.state.StateEventManager
import com.utsman.core.LocationManager
import com.utsman.core.extensions.asFlowStateEvent
import com.utsman.core.extensions.mapStateEvent
import com.utsman.core.state.FlowState
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.LocationWebServices
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class HomeRepositoryImpl(
    private val locationManager: LocationManager,
    private val locationServices: LocationWebServices
) : HomeRepository {

    private val _locationResult = MutableStateEventManager<Location>()
    override val locationResult: StateEventManager<Location>
        get() = _locationResult

    private val _initialLocation: MutableStateFlow<StateEvent<LocationData>> = MutableStateFlow(StateEvent.Idle())
    override val initialLocation: FlowState<LocationData>
        get() = _initialLocation

    private val _locationFrom: MutableStateFlow<StateEvent<LocationData>> = MutableStateFlow(StateEvent.Idle())
    override val locationFrom: FlowState<LocationData>
        get() = _locationFrom

    private val _locationDestination: MutableStateFlow<StateEvent<LocationData>> = MutableStateFlow(StateEvent.Idle())
    override val locationDestination: FlowState<LocationData>
        get() = _locationDestination

    override suspend fun reverseCurrentLocation(location: Location) {
        val coordinateString = "${location.latitude},${location.longitude}"
        _initialLocation.value = StateEvent.Loading()
        locationServices.reverseLocation(coordinateString).asFlowStateEvent {
            Mapper.mapReverseLocationResponseToData(it)
        }.catch {
            emit(StateEvent.Failure(it))
        }.collect {
            _initialLocation.value = it
        }
    }

    override fun setFromLocation(locationData: LocationData) {
        _locationFrom.value = StateEvent.Success(locationData)
    }

    override fun setDestinationLocation(locationData: LocationData) {
        _locationDestination.value = StateEvent.Success(locationData)
    }

    override suspend fun getLocation() {
        locationManager.getLocationFlow()
            .mapStateEvent()
            .collect(_locationResult)
    }


}