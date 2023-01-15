package com.utsman.ojeku.cust.search

import android.location.Location
import androidx.lifecycle.*
import com.utsman.core.extensions.asFlowStateEvent
import com.utsman.core.extensions.convertEventToSubscriber
import com.utsman.core.extensions.toLatLng
import com.utsman.core.state.MutableStateEventManager
import com.utsman.core.state.StateEvent
import com.utsman.core.state.StateEventManager
import com.utsman.core.state.StateEventSubscriber
import com.utsman.locationapi.LocationWebServices
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.StateLocationList
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class SearchLocationViewModel(
    private val searchLocationRepository: SearchLocationRepository
) : ViewModel() {

    private val searchLocationSafeScope =
        viewModelScope + CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }

    val searchLocationState =
        searchLocationRepository.locationState.asLiveData(searchLocationSafeScope.coroutineContext)

    private val _fromLocationData = MutableLiveData(LocationData())
    val fromLocationData: LiveData<LocationData>
        get() = _fromLocationData

    private val _destLocationData = MutableLiveData(LocationData())
    val destLocationData: LiveData<LocationData>
        get() = _destLocationData

    var searchType: Int = 1
    var isEnableFromSearch = true
    var isEnableDestSearch = true

    var currentLocation: Location = Location("").apply {
        latitude = 0.0
        longitude = 0.0
    }

    fun setFromLocationData(locationData: LocationData) {
        _fromLocationData.value = locationData
    }

    fun setDestLocationData(locationData: LocationData) {
        _destLocationData.value = locationData
    }

    fun getLocations(name: String, location: Location) = searchLocationSafeScope.launch {
        val coordinate = "${location.latitude},${location.longitude}"
        searchLocationRepository.searchLocation(name, coordinate)
    }
}