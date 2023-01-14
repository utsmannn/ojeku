package com.utsman.ojeku.cust.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utsman.core.extensions.asFlowStateEvent
import com.utsman.core.extensions.convertEventToSubscriber
import com.utsman.core.state.MutableStateEventManager
import com.utsman.core.state.StateEvent
import com.utsman.core.state.StateEventManager
import com.utsman.core.state.StateEventSubscriber
import com.utsman.locationapi.LocationWebServices
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.StateLocationList
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.launch

class SearchLocationViewModel(
    private val webServices: LocationWebServices
) : ViewModel() {

    private val _locationListLive: MutableLiveData<StateLocationList> = MutableLiveData(StateEvent.Idle())
    val locationListLive: LiveData<StateLocationList>
        get() = _locationListLive

    private val _locationStateManager: MutableStateEventManager<List<LocationData>> = MutableStateEventManager()
    private val locationStateManager: StateEventManager<List<LocationData>>
        get() = _locationStateManager

    var fromLocation: LocationData = LocationData()
    var destLocation: LocationData = LocationData()

    fun getLocations(name: String) = locationStateManager.createScope(viewModelScope).launch {
        val coordinateString = "-6.2842147,106.8447178"
        webServices.searchLocation(name, coordinateString).asFlowStateEvent {
            Mapper.mapLocationResponseToData(it)
        }.collect(_locationStateManager)
    }

    fun subscribeLocationStateManager(subscriber: StateEventSubscriber<List<LocationData>>) {
        convertEventToSubscriber(locationStateManager, subscriber)
    }
}